package com.yuruiyin.richeditor.callback;

import com.yuruiyin.richeditor.span.BlockImageSpan;

/**
 * Title: 图片（ImageSpan）短按监听器
 * Description: 譬如若ImageSpan是相册的图片的话，则点击查看大图，
 * 若是自定义view（比如左图右文的链接）的imageSpan，则点击可能是跳转到具体的详情页面
 *
 * @author yuruiyin
 * @version 2019-05-05
 */
public interface OnImageClickListener {

    /**
     * 图片(ImageSpan)被点击时回调
     *
     * @param blockImageSpan 里头包含调用方自己传进来的实体数据
     */
    void onClick(BlockImageSpan blockImageSpan);

}
