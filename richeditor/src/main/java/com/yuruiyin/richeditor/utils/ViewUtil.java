package com.yuruiyin.richeditor.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    /**
     * 通过canvas将view转化为bitmap
     * @param view 指定view
     * @return bitmap
     */
    public static Bitmap getBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // 如果不设置canvas画布为白色，则生成透明
//        c.drawColor(Color.WHITE);
//        view.layout(0, 0, w, h);

        view.draw(canvas);
        return bmp;
    }

}
