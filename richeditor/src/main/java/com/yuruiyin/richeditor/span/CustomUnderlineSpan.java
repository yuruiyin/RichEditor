package com.yuruiyin.richeditor.span;

import android.text.style.UnderlineSpan;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;
import com.yuruiyin.richeditor.model.IInlineSpan;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-30
 */
public class CustomUnderlineSpan extends UnderlineSpan implements IInlineSpan {

    private String type;

    public CustomUnderlineSpan() {
        type = RichTypeEnum.UNDERLINE;
    }

    @Override
    public String getType() {
        return type;
    }

}
