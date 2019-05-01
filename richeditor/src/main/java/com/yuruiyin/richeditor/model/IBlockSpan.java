package com.yuruiyin.richeditor.model;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-30
 */
public interface IBlockSpan {

    @RichTypeEnum String getType();

    String getContent();

}
