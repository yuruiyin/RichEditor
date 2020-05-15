package com.yuruiyin.richeditor.utils;

public class RichEditorGlobalHelper {

    private RichEditorGlobalHelper() {

    }

    // 是否开启日志
    private static boolean mIsLogEnable;

    public static void setIsLogEnable(boolean isLogEnable) {
        mIsLogEnable = isLogEnable;
    }

    public static boolean isLogEnable() {
        return mIsLogEnable;
    }

}
