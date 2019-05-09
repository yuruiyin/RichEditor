package com.yuruiyin.richeditor.span;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class BoldStyleSpan extends StyleSpan implements IInlineSpan {

    private String type;

    public BoldStyleSpan() {
        super(Typeface.BOLD);
        type = RichTypeEnum.BOLD;
    }

    @Override
    public String getType() {
        return type;
    }

}
