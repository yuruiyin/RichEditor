package com.yuruiyin.richeditor.enumtype;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({BlockTypeEnum.HEADLINE, BlockTypeEnum.BLOCK_QUOTE})
public @interface BlockTypeEnum {

    /**
     * 标题
     */
    String HEADLINE = "headline";

    /**
     * 引用
     */
    String BLOCK_QUOTE = "block_quote";

}
