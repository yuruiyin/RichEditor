package com.yuruiyin.richeditor.span;

import android.view.View;

/**
 * Title: 支持长按的span
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-05
 */
public interface LongClickableSpan {

    /**
     * 短按事件
     *
     * @param widget view
     */
    void onClick(View widget);

}
