package com.yuruiyin.richeditor.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.yuruiyin.richeditor.RichEditText;
import com.yuruiyin.richeditor.callback.OnImageClickListener;

/**
 * Title: 段落ImageSpan
 * Description: 如图片、视频封面、以及自定义布局等，支持响应短按和长按事件
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class BlockImageSpan extends CenterImageSpan implements LongClickableSpan {

    private float x;
    private float y;

    /**
     * 指定当前段落ImageSpan的类型（如图片、链接、视频封面、自定义view等）
     * 注意：由于该类型组件无法预知，所以该字段值外部自行维护
     */
    private String mSpanType;

    /**
     * 当前ImageSpan所包含的实体（如图片、链接、视频封面、自定义view等相关实体数据）
     */
    private Object mSpanObject;

    private OnImageClickListener mOnImageClickListener;

    public BlockImageSpan(Context context, int resourceId, @NonNull String spanType, Object spanObject) {
        super(context, resourceId);
        initData(spanType, spanObject);
    }

    public BlockImageSpan(Context context, Bitmap bitmap, @NonNull String spanType, Object spanObject) {
        super(context, bitmap);
        initData(spanType, spanObject);
    }

    public BlockImageSpan(Drawable drawable, @NonNull String spanType, Object spanObject) {
        super(drawable);
        initData(spanType, spanObject);
    }

    public BlockImageSpan(Context context, Uri uri, @NonNull String spanType, Object spanObject) {
        super(context, uri);
        initData(spanType, spanObject);
    }

    private void initData(@NonNull String spanType, Object spanObject) {
        mSpanType = spanType;
        mSpanObject = spanObject;
    }

    public void setOnClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    public String getSpanType() {
        return mSpanType;
    }

    public Object getSpanObject() {
        return mSpanObject;
    }

    @Override
    public void onClick(View widget) {
        if (mOnImageClickListener != null) {
            mOnImageClickListener.onClick(this);
        }
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        this.x = x;
        this.y = top;
    }

    public boolean clicked(int touchX, int touchY) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Rect rect = drawable.getBounds();
            return touchX <= rect.right + x && touchX >= rect.left + x
                    && touchY <= rect.bottom + y && touchY >= rect.top + y;
        }
        return false;
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = super.getDrawable();
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int maxWidth = RichEditText.globalRichEditTextWidth;

        // 防止drawable宽度过大，超过编辑器的宽度
        if (width > maxWidth) {
            float scale = ((float) maxWidth / width);
            drawable.setBounds(0, 0, maxWidth, (int) (height * scale));
        }

        return drawable;
    }

}