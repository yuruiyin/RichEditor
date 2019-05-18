package com.yuruiyin.richeditor.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Title: 剪贴板工具类
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-18
 */
public class ClipboardUtil {
    private static ClipboardUtil mClipboardUtil;

    private static ClipboardManager mClipboardManager;

    private Context mContext;

    private ClipboardUtil(Context context) {
        mContext = context;
        mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static ClipboardUtil getInstance(Context context) {
        if (mClipboardUtil == null) {
            mClipboardUtil = new ClipboardUtil(context);
        }
        return mClipboardUtil;
    }

    public void copy(String content) {
        mClipboardManager.setPrimaryClip(ClipData.newPlainText("text", content));
    }

    public String getClipboardText() {
        if (!mClipboardManager.hasPrimaryClip()) {
            return "";
        }

        StringBuilder resString = new StringBuilder();
        ClipData clipData = mClipboardManager.getPrimaryClip();
        if (clipData == null) {
            return "";
        }

        int itemCount = clipData.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            ClipData.Item item = clipData.getItemAt(i);
            resString.append(item.coerceToText(mContext));
        }
        return resString.toString();
    }


}
