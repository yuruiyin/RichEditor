package com.yuruiyin.richeditor.sample.model

import com.yuruiyin.richeditor.model.IBlockImageSpanObtainObject
import com.yuruiyin.richeditor.sample.enumtype.BlockImageSpanType

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-10
 */
data class VideoVm(val path: String, val id: String): IBlockImageSpanObtainObject {
    override fun getType(): String = BlockImageSpanType.VIDEO
}