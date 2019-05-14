package com.yuruiyin.richeditor;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;

import com.hanks.lineheightedittext.LineHeightEditText;
import com.yuruiyin.richeditor.callback.OnImageClickListener;
import com.yuruiyin.richeditor.enumtype.FileTypeEnum;
import com.yuruiyin.richeditor.ext.LongClickableLinkMovementMethod;
import com.yuruiyin.richeditor.model.BlockImageSpanVm;
import com.yuruiyin.richeditor.model.RichEditorBlock;
import com.yuruiyin.richeditor.model.StyleBtnVm;
import com.yuruiyin.richeditor.span.BlockImageSpan;
import com.yuruiyin.richeditor.utils.FileUtil;
import com.yuruiyin.richeditor.utils.ViewUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: 自定义EditText，可监听光标位置变化
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichEditText extends LineHeightEditText {

    private static final String TAG = "RichEditText";

    // 宽度撑满编辑区的ImageSpan需要减去的一个值，为了防止ImageSpan碰到边界导致的重复绘制的问题
    private static final int IMAGE_SPAN_MINUS_VALUE = 6;

    private int imageSpanPaddingTop;
    private int imageSpanPaddingBottom;
    private int imageSpanPaddingRight;
    // 内部限制的图片最大高度
    private int internalImageMaxHeight;
    // 视频标识图标资源id
    private int videoIconResourceId;

    /**
     * EditText的宽度
     */
    public static int gRichEditTextWidthWithoutPadding;

    private RichInputConnectionWrapper mRichInputConnection;

    private Context mContext;

    private RichUtils mRichUtils;

    public interface OnSelectionChangedListener {
        /**
         * 光标位置改变回调
         *
         * @param curPos 新的光标位置
         */
        void onChange(int curPos);
    }

    /**
     * EditText监听复制、粘贴、剪切事件回调的接口
     */
    public interface IClipCallback {
        /**
         * 剪切回调
         */
        void onCut();

        /**
         * 复制回调
         */
        void onCopy();

        /**
         * 粘贴回调
         */
        void onPaste();
    }

    /**
     * 光标位置变化监听器
     */
    private OnSelectionChangedListener mOnSelectionChangedListener;

    public RichEditText(Context context) {
        super(context);
        init(context, null);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RichEditText);
            videoIconResourceId = ta.getResourceId(R.styleable.RichEditText_re_video_mark_resource_id, R.drawable.default_video_icon);
            ta.recycle();
        }

        mContext = context;
        imageSpanPaddingTop = (int) mContext.getResources().getDimension(R.dimen.rich_editor_image_span_padding_top);
        imageSpanPaddingBottom = (int) mContext.getResources().getDimension(R.dimen.rich_editor_image_span_padding_bottom);
        imageSpanPaddingRight = (int) mContext.getResources().getDimension(R.dimen.rich_editor_image_span_padding_right);
        internalImageMaxHeight = (int) mContext.getResources().getDimension(R.dimen.rich_editor_image_max_height);

        mRichInputConnection = new RichInputConnectionWrapper(null, true);
        setMovementMethod(new LongClickableLinkMovementMethod());
        requestFocus();
        setSelection(0);

        if (!(mContext instanceof Activity)) {
            Log.e(TAG, "context is not activity context!");
            return;
        }

        mRichUtils = new RichUtils((Activity) context, this);
    }

    private int getWidthWithoutPadding() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - IMAGE_SPAN_MINUS_VALUE;
    }

    public void initStyleButton(StyleBtnVm styleBtnVm) {
        mRichUtils.initStyleButton(styleBtnVm);
    }

    public void insertBlockImage(Drawable drawable, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        if (!(mContext instanceof Activity)) {
            Log.e(TAG, "context is not activity context!");
            return;
        }

        int originWidth = drawable.getIntrinsicWidth();
        int originHeight = drawable.getIntrinsicHeight();

        // 这里减去一个值是为了防止部分手机（如华为Mate-10）ImageSpan右侧超出编辑区的时候，会导致ImageSpan被重复绘制的问题
        int editTextWidth = getWidthWithoutPadding();
        int imageWidth = blockImageSpanVm.getWidth();
        int resImageWidth = imageWidth > editTextWidth ? editTextWidth : imageWidth;
        int imageMaxHeight = blockImageSpanVm.getMaxHeight() > internalImageMaxHeight
            ? internalImageMaxHeight : blockImageSpanVm.getMaxHeight();
        int resImageHeight = (int) (originHeight * 1.0 / originWidth * resImageWidth);
        resImageHeight = resImageHeight > imageMaxHeight ? imageMaxHeight : resImageHeight;

        Activity activity = (Activity) mContext;
        View imageItemView = activity.getLayoutInflater().inflate(R.layout.rich_editor_image, null);
        ImageView imageView = imageItemView.findViewById(R.id.image);
        ImageView ivVideoIcon = imageItemView.findViewById(R.id.ivVideoIcon);
        imageView.setImageDrawable(drawable);

        if (blockImageSpanVm.isVideo() && videoIconResourceId != 0) {
            // 视频封面
            Drawable videoIconDrawable = AppCompatResources.getDrawable(mContext, videoIconResourceId);
            if (videoIconDrawable != null) {
                ivVideoIcon.setVisibility(VISIBLE);
                ivVideoIcon.setImageDrawable(videoIconDrawable);
                ViewGroup.LayoutParams layoutParams = ivVideoIcon.getLayoutParams();
                layoutParams.width = videoIconDrawable.getIntrinsicWidth();
                layoutParams.height = videoIconDrawable.getIntrinsicHeight();
            } else {
                ivVideoIcon.setVisibility(GONE);
            }
        } else {
            ivVideoIcon.setVisibility(GONE);
        }

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = resImageWidth;
        layoutParams.height = resImageHeight;

        ViewUtil.layoutView(
            imageItemView,
            resImageWidth + imageSpanPaddingRight,
            resImageHeight + imageSpanPaddingTop + imageSpanPaddingBottom
        );

        BlockImageSpan blockImageSpan = new BlockImageSpan(
            mContext, ViewUtil.getBitmap(imageItemView), blockImageSpanVm
        );
        mRichUtils.insertBlockImageSpan(blockImageSpan);

        // 设置图片点击监听器
        blockImageSpan.setOnClickListener(onImageClickListener);
    }

    private void insertBlockImageInternal(Uri uri, @NonNull BlockImageSpanVm blockImageSpanVm,
                                          OnImageClickListener onImageClickListener) {
        if (uri == null) {
            Log.e(TAG, "uri is null");
            return;
        }

        try {
            InputStream is = mContext.getContentResolver().openInputStream(
                uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
            is.close();
            insertBlockImage(drawable, blockImageSpanVm, onImageClickListener);
        } catch (Exception e) {
            Log.e(TAG, "Failed to loaded content " + uri, e);
        }
    }

    /**
     * 根据uri插入图片或视频封面
     * @param uri 文件uri
     * @param blockImageSpanVm 相关实体
     * @param onImageClickListener 图片点击事件监听器
     */
    public void insertBlockImage(Uri uri, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        if (uri == null) {
            Log.e(TAG, "uri is null");
            return;
        }

        insertBlockImage(FileUtil.getFileRealPath(mContext, uri), blockImageSpanVm, onImageClickListener);
    }

    /**
     * 根据文件路径插入图片或视频封面
     *
     * @param filePath             图片(或视频)文件路径，类似 /storage/emulated/0/Pictures/17173/1553236560146.jpg
     * @param blockImageSpanVm     相关实体
     * @param onImageClickListener 图片点击事件监听器
     */
    public void insertBlockImage(String filePath, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "file path is empty");
            return;
        }

        String fileType = FileUtil.getFileType(filePath);
        switch (fileType) {
            case FileTypeEnum.VIDEO:
                Bitmap coverBitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                blockImageSpanVm.setVideo(true);
                insertBlockImage(coverBitmap, blockImageSpanVm, onImageClickListener);
                break;
            case FileTypeEnum.IMAGE:
                File file = new File(filePath);
                blockImageSpanVm.setVideo(false);
                insertBlockImageInternal(Uri.fromFile(file), blockImageSpanVm, onImageClickListener);
                break;
            default:
                Log.e(TAG, "file type is illegal");
                break;
        }
    }

    public void insertBlockImage(@DrawableRes int resourceId, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        try {
            Drawable drawable = AppCompatResources.getDrawable(mContext, resourceId);
            insertBlockImage(drawable, blockImageSpanVm, onImageClickListener);
        } catch (Exception e) {
            Log.e(TAG, "Unable to find resource: " + resourceId);
        }
    }

    public void insertBlockImage(Bitmap bitmap, @NonNull BlockImageSpanVm blockImageSpanVm,
                                 OnImageClickListener onImageClickListener) {
        Drawable drawable = mContext != null
            ? new BitmapDrawable(mContext.getResources(), bitmap)
            : new BitmapDrawable(bitmap);
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
        insertBlockImage(drawable, blockImageSpanVm, onImageClickListener);
    }

    /**
     * 获取编辑器中的内容
     * @return 编辑的内容
     */
    public List<RichEditorBlock> getContent() {
        return mRichUtils.getContent();
    }

    public RichUtils getRichUtils() {
        return mRichUtils;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        gRichEditTextWidthWithoutPadding = getWidthWithoutPadding();
    }

    /**
     * 设置软键盘删除按键监听器
     *
     * @param backspaceListener 软键盘删除按键监听器
     */
    protected void setBackspaceListener(RichInputConnectionWrapper.BackspaceListener backspaceListener) {
        mRichInputConnection.setBackspaceListener(backspaceListener);
    }

    /**
     * 注册光标位置监听器
     *
     * @param listener 光标位置变化监听器
     */
    protected void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.mOnSelectionChangedListener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onChange(selEnd);
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (!(mContext instanceof IClipCallback)) {
            return super.onTextContextMenuItem(id);
        }

        IClipCallback context = (IClipCallback) mContext;

        switch (id) {
            case android.R.id.cut:
                context.onCut();
                break;
            case android.R.id.copy:
                context.onCopy();
                break;
            case android.R.id.paste:
                context.onPaste();
                //粘贴特殊处理
                return true;
            default:
                break;
        }

        return super.onTextContextMenuItem(id);
    }

    /**
     * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
     * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        mRichInputConnection.setTarget(super.onCreateInputConnection(outAttrs));
        return mRichInputConnection;
    }

}
