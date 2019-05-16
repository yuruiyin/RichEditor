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
@StringDef({FileTypeEnum.STATIC_IMAGE, FileTypeEnum.VIDEO, FileTypeEnum.AUDIO})
public @interface FileTypeEnum {

    /**
     * 静态图, 包括png，jpg等
     */
    String STATIC_IMAGE = "static_image";

    /**
     * 动图
     */
    String GIF = "gif";

    /**
     * 视频
     */
    String VIDEO = "video";

    /**
     * 音频
     */
    String AUDIO = "audio";

}
