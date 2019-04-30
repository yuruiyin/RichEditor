package com.yuruiyin.richeditor.enumtype;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title: 行内样式类型
 * Description: 包含加粗、斜体、删除线、下划线等
 *
 * @author yuruiyin
 * @version 2019-04-29
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({InlineStyleEnum.BOLD, InlineStyleEnum.ITALIC, InlineStyleEnum.STRIKE_THROUGH, InlineStyleEnum.UNDERLINE})
public @interface InlineStyleEnum {

    /**
     * 加粗
     */
    String BOLD = "bold";

    /**
     * 斜体
     */
    String ITALIC = "italic";

    /**
     * 删除线
     */
    String STRIKE_THROUGH = "strike_through";

    /**
     * 下划线
     */
    String UNDERLINE = "underline";

}
