package com.yuruiyin.richeditor.enumtype;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title: 文件类型
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-13
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({FileTypeEnum.IMAGE, FileTypeEnum.VIDEO, FileTypeEnum.AUDIO})
public @interface FileTypeEnum {

    /**
     * 图片
     */
    String IMAGE = "image";

    /**
     * 视频
     */
    String VIDEO = "video";

    /**
     * 音频
     */
    String AUDIO = "audio";

}
