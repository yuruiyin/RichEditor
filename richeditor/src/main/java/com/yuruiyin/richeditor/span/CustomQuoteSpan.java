package com.yuruiyin.richeditor.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;

import com.yuruiyin.richeditor.R;
import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

/**
 * Title: 自定义的引用span
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-30
 */
public class CustomQuoteSpan extends AbsoluteSizeSpan implements
    IBlockSpan, LeadingMarginSpan, LineBackgroundSpan, LineHeightSpan {

    private Context mContext;
    private String type;
    // 引用背景色
    private int mBgColor;
    // 引用左侧竖线颜色
    private int mStripeColor;
    // 引用竖线宽度
    private int mStripeWidth;
    // 引用竖线高度
    private int mStripeHeight;
    // 引用竖线与文本的间距
    private int mGapWidth;

    public CustomQuoteSpan(Context context) {
        super((int) context.getResources().getDimension(R.dimen.rich_editor_quote_text_size));
//        super(
//                context.getResources().getColor(R.color.rich_editor_quote_stripe_color),
//                (int) context.getResources().getDimension(R.dimen.rich_editor_quote_stripe_width),
//                (int) context.getResources().getDimension(R.dimen.rich_editor_quote_gap_width)
//        );

        mContext = context;
        type = RichTypeEnum.BLOCK_QUOTE;
        mBgColor = context.getResources().getColor(R.color.rich_editor_quote_bg_color);
        mStripeColor = context.getResources().getColor(R.color.rich_editor_quote_stripe_color);
        mStripeWidth = (int) mContext.getResources().getDimension(R.dimen.rich_editor_quote_stripe_width);
        mGapWidth = (int) mContext.getResources().getDimension(R.dimen.rich_editor_quote_gap_width);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mStripeWidth + mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom,
                                  CharSequence text, int start, int end, boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();

        p.setStyle(Paint.Style.FILL);
        p.setColor(mStripeColor);

        c.drawRect(x, top, x + dir * mStripeWidth, bottom, p);

        mStripeHeight = bottom - top;

        p.setStyle(style);
        p.setColor(color);
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
        final int paintColor = p.getColor();
        p.setColor(mBgColor);
        c.drawRect(new Rect(left, top, right, bottom), p);
        p.setColor(paintColor);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        if (end == ((Spanned) text).getSpanEnd(this)) {
            int ht = mStripeHeight;

            int need = ht - (lineHeight + fm.descent - fm.ascent - spanstartv);
            if (need > 0) {
                fm.descent += need;
            }

            need = ht - (lineHeight + fm.bottom - fm.top - spanstartv);
            if (need > 0) {
                fm.bottom += need;
            }
        }
    }

}
