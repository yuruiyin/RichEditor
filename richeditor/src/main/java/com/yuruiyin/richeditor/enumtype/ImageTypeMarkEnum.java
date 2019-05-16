package com.yuruiyin.richeditor.enumtype;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title: 图片类型标识（gif、长图）枚举类
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-15
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({ImageTypeMarkEnum.GIF, ImageTypeMarkEnum.LONG})
public @interface ImageTypeMarkEnum {

    String GIF = "GIF";

    String LONG = "长图";

}
