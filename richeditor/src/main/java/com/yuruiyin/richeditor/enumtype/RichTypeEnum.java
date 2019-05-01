package com.yuruiyin.richeditor.enumtype;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-30
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({RichTypeEnum.BOLD, RichTypeEnum.ITALIC, RichTypeEnum.STRIKE_THROUGH, RichTypeEnum.UNDERLINE,
    RichTypeEnum.HEADLINE, RichTypeEnum.BLOCK_QUOTE})
public @interface RichTypeEnum {

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

    /**
     * 标题
     */
    String HEADLINE = "headline";

    /**
     * 引用
     */
    String BLOCK_QUOTE = "block_quote";

}
