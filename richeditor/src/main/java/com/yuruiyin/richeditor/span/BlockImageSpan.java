package com.yuruiyin.richeditor.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Title: 段落ImageSpan
 * Description: 如图片、视频封面、以及自定义布局等
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class BlockImageSpan extends CenterImageSpan {

    public BlockImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public BlockImageSpan(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public BlockImageSpan(Drawable drawable) {
        super(drawable);
    }

}