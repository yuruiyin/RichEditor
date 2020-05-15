package com.yuruiyin.richeditor.enumtype;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({UndoRedoActionTypeEnum.ADD, UndoRedoActionTypeEnum.DELETE, UndoRedoActionTypeEnum.CHANGE})
public @interface UndoRedoActionTypeEnum {

    /**
     * 增加字符
     */
    String ADD = "add";

    /**
     * 删除字符
     */
    String DELETE = "delete";

    /**
     * 修改样式
     */
    String CHANGE = "change";

}
