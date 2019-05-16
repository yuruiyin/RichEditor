package com.yuruiyin.richeditor.sample.model

import com.yuruiyin.richeditor.model.RichEditorBlock

/**
 * Title: 草稿中保存的编辑器段落
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-16
 */
class DraftEditorBlock {
    var blockType: String? = null
    var text: String? = null
    var image: ImageVm? = null
    var video: VideoVm? = null
    var divider: DividerVm? = null
    var game: GameVm? = null
    var inlineStyleEntities: List<RichEditorBlock.InlineStyleEntity>? = null

}