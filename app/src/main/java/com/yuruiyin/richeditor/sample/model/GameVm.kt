package com.yuruiyin.richeditor.sample.model

import com.yuruiyin.richeditor.model.IBlockImageSpanObtainObject
import com.yuruiyin.richeditor.sample.enumtype.BlockImageSpanType

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-16
 */
data class GameVm(val id: Long, val name: String): IBlockImageSpanObtainObject {
    override fun getType(): String  = BlockImageSpanType.GAME
}