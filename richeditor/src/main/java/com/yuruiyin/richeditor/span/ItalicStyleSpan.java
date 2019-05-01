package com.yuruiyin.richeditor.span;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;
import com.yuruiyin.richeditor.model.IInlineSpan;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class ItalicStyleSpan extends StyleSpan implements IInlineSpan {

    private String type;

    public ItalicStyleSpan() {
        super(Typeface.ITALIC);
        type = RichTypeEnum.ITALIC;
    }

    @Override
    public String getType() {
        return type;
    }
}
