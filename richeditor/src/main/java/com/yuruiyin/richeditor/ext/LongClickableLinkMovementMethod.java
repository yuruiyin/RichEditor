package com.yuruiyin.richeditor.ext;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

import com.yuruiyin.richeditor.span.BlockImageSpan;

/**
 * Title: 支持长按的LinkMovementMethod
 * Description: 譬如当ImageSpan支持响应点击和长按事件
 *
 * @author yuruiyin
 * @version 2019-05-06
 */
public class LongClickableLinkMovementMethod extends LinkMovementMethod {

    private BlockImageSpan mPressedSpan;

    private void safeRemoveSpan(Spannable spannable) {
        if (!spannable.toString().isEmpty()) {
            Selection.removeSelection(spannable);
        }
    }

    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressedSpan = getPressedSpan(textView, spannable, event);
//                if (mPressedSpan != null) {  //点击span区域
//                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
//                        spannable.getSpanEnd(mPressedSpan));
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                BlockImageSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan = null;
                    safeRemoveSpan(spannable);
                }
                break;
            default:
                if (mPressedSpan != null) {
                    if (MotionEvent.ACTION_UP == action) {
                        mPressedSpan.onClick(textView);
                    }
//                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
//                safeRemoveSpan(spannable);
                break;
        }

        return true;
    }

    private BlockImageSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX() - textView.getTotalPaddingLeft() + textView.getScrollX();
        int y = (int) event.getY() - textView.getTotalPaddingTop() + textView.getScrollY();

        Layout layout = textView.getLayout();
        int position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x);

        BlockImageSpan[] blockImageSpans = spannable.getSpans(position, position, BlockImageSpan.class);
        BlockImageSpan touchedSpan = null;
        if (blockImageSpans.length > 0 && positionWithinTag(position, spannable, blockImageSpans[0])
            && blockImageSpans[0].clicked(x, y)) {
            touchedSpan = blockImageSpans[0];
        }

        return touchedSpan;
    }

    private boolean positionWithinTag(int position, Spannable spannable, Object tag) {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag);
    }
}
