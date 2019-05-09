package com.yuruiyin.richeditor.sample.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-08
 */

fun View.getBitmap(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    return bmp
}

fun View.layout(width: Int, height: Int) {
    // 指定整个View的大小 参数是左上角 和右下角的坐标
    layout(0, 0, width, height)
    val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
    // 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
    measure(measuredWidth, measuredHeight)
    layout(0, 0, getMeasuredWidth(), getMeasuredHeight())
}
