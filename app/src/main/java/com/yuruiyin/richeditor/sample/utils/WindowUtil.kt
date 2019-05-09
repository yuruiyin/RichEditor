package com.yuruiyin.richeditor.sample.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-08
 */
class WindowUtil {

    companion object {
        /**
         * 获取设备屏幕宽高（pixel值）
         * @param context
         * @return
         */
        fun getScreenSize(context: Context): IntArray {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            return intArrayOf(outMetrics.widthPixels, outMetrics.heightPixels)
        }
    }

}