package com.yuruiyin.richeditor.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Title: 行内ImageSpan
 * Description: 如@xxx, #xxx#之类的
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class InlineImageSpan extends CenterImageSpan {

    public InlineImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public InlineImageSpan(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public InlineImageSpan(Drawable drawable) {
        super(drawable);
    }

}
