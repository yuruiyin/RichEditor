package com.yuruiyin.richeditor.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.yuruiyin.richeditor.model.InlineImageSpanVm;

/**
 * Title: 行内ImageSpan，高度固定为文字的行高（不支持自定义高度的行内ImageSpan）
 * Description: 如@xxx, #xxx#之类的
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class InlineImageSpan extends CenterImageSpan {

    private InlineImageSpanVm inlineImageSpanVm;

    public InlineImageSpan(Context context, Bitmap bitmap, @NonNull InlineImageSpanVm inlineImageSpanVm) {
        super(context, bitmap);
        initData(inlineImageSpanVm);
    }

    public InlineImageSpan(Drawable drawable, @NonNull InlineImageSpanVm inlineImageSpanVm) {
        super(drawable);
        initData(inlineImageSpanVm);
    }

    private void initData(@NonNull InlineImageSpanVm inlineImageSpanVm) {
        this.inlineImageSpanVm = inlineImageSpanVm;
    }

    public InlineImageSpanVm getInlineImageSpanVm() {
        return inlineImageSpanVm;
    }
}
