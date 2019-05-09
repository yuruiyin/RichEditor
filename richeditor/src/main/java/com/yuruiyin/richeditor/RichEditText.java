package com.yuruiyin.richeditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import com.hanks.lineheightedittext.LineHeightEditText;
import com.yuruiyin.richeditor.callback.OnImageClickListener;
import com.yuruiyin.richeditor.ext.LongClickableLinkMovementMethod;
import com.yuruiyin.richeditor.model.BlockImageSpanVm;
import com.yuruiyin.richeditor.model.StyleBtnVm;
import com.yuruiyin.richeditor.span.BlockImageSpan;
import com.yuruiyin.richeditor.utils.ViewUtil;

import java.io.InputStream;

/**
 * Title: 自定义EditText，可监听光标位置变化
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichEditText extends LineHeightEditText {

    private static final String TAG = "RichEditText";

    private int imageSpanPaddingTop;
    private int imageSpanPaddingBottom;
    private int imageSpanPaddingRight;
    // 内部限制的图片最大高度
    private int internalImageMaxHeight;

    /**
     * EditText的宽度
     */
    public static int globalRichEditTextWidth;

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
        init(context);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
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
        int editTextWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - 6;
        int imageWidth = blockImageSpanVm.getWidth();
        int resImageWidth = imageWidth > editTextWidth ? editTextWidth : imageWidth;
        int imageMaxHeight = blockImageSpanVm.getMaxHeight() > internalImageMaxHeight
                ? internalImageMaxHeight : blockImageSpanVm.getMaxHeight();
        int resImageHeight = (int) (originHeight * 1.0 / originWidth * resImageWidth);
        resImageHeight = resImageHeight > imageMaxHeight ? imageMaxHeight : resImageHeight;

        Activity activity = (Activity) mContext;
        View imageItemView = activity.getLayoutInflater().inflate(R.layout.rich_editor_image, null);
        ImageView imageView = imageItemView.findViewById(R.id.image);
        imageView.setImageDrawable(drawable);

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = resImageWidth;
        layoutParams.height = resImageHeight;

        ViewUtil.layoutView(
                imageItemView,
                resImageWidth + imageSpanPaddingRight,
                resImageHeight + imageSpanPaddingTop + imageSpanPaddingBottom
        );

        BlockImageSpan blockImageSpan = new BlockImageSpan(
                mContext, ViewUtil.getBitmap(imageItemView), blockImageSpanVm.getType(), blockImageSpanVm.getSpanObject()
        );
        mRichUtils.insertBlockImageSpan(blockImageSpan);

        // 设置图片点击监听器
        blockImageSpan.setOnClickListener(onImageClickListener);
    }

    public void insertBlockImage(Uri uri, @NonNull BlockImageSpanVm blockImageSpanVm,
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        globalRichEditTextWidth = getMeasuredWidth();
    }

    public void setRichUtils(RichUtils richUtils) {
        mRichUtils = richUtils;
    }

    public RichUtils getRichUtils() {
        return mRichUtils;
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
