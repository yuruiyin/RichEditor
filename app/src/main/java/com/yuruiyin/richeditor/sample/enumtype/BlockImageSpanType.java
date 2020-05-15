package com.yuruiyin.richeditor.sample.enumtype;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-08
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({BlockImageSpanType.IMAGE, BlockImageSpanType.VIDEO, BlockImageSpanType.DIVIDER})
public @interface BlockImageSpanType {

    /**
     * 相册图片
     */
    String IMAGE = "image";

    /**
     * 视频
     */
    String VIDEO = "video";

    /**
     * 分割线
     */
    String DIVIDER = "divider";

    /**
     * 游戏
     */
    String GAME = "game";
}
