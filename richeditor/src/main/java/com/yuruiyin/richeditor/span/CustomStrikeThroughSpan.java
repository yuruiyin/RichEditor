package com.yuruiyin.richeditor.span;

import android.text.style.StrikethroughSpan;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-30
 */
public class CustomStrikeThroughSpan extends StrikethroughSpan implements IInlineSpan {

    private String type;

    public CustomStrikeThroughSpan() {
        type = RichTypeEnum.STRIKE_THROUGH;
    }

    @Override
    public String getType() {
        return type;
    }
}
