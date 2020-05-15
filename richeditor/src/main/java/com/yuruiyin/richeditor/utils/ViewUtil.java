package com.yuruiyin.richeditor.utils;

import android.view.View;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-09
 */
public class ViewUtil {

    /**
     * 设置指定view的实际大小
     * @param view 指定控件
     * @param width view的实际宽
     * @param height view的实际高
     */
    public static void layoutView(View view, int width, int height) {
        // 指定整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        // 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

}
