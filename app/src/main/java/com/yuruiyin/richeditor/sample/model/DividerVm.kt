package com.yuruiyin.richeditor.sample.model

import com.yuruiyin.richeditor.model.IBlockImageSpanObtainObject
import com.yuruiyin.richeditor.sample.enumtype.BlockImageSpanType

/**
 * Title: 分割线实体
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-14
 */
class DividerVm: IBlockImageSpanObtainObject {
    override fun getType(): String = BlockImageSpanType.DIVIDER
}