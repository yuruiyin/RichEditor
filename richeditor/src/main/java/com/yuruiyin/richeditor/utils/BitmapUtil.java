package com.yuruiyin.richeditor.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-29
 */
public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    /**
     * 压缩bitmap（为了解决EditText（如华为手机）插入多张ImageSpan之后的滑动卡顿问题）
     * @param bitmap 原始bitmap
     * @return 压缩后的bitmap
     */
    private static Bitmap compress(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        bitmap.recycle();
        byte[] bytes = outputStream.toByteArray();
        Bitmap resBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        LogUtil.d(TAG, "bitmap字节数： " + resBitmap.getByteCount());
        return resBitmap;
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

        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);

        // 这里一定要设置canvas画布为白色，因为bitmap config是RGB_565,不透明的
        // 如果插入有透明属性的View，而且不设置画布为白色，就会出现黑边。
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return compress(bmp);
//        LogUtil.d(TAG, "未压缩-bitmap字节数： " + bmp.getByteCount());
//        return bmp;
    }

    /**
     * Returns a power of two size for the given target capacity.
     * 参考HashMap
     */
    public static int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : n + 1;
    }

    /**
     * 计算Bitmap的inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int expectWidth) {
        // Raw height and width of image
        return tableSizeFor(options.outWidth / expectWidth);
    }

    public static Bitmap decodeSampledBitmapFromFilePath(String filePath, int expectWidth) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, expectWidth);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 选择bitmap
     *
     * @param filePath 文件路径
     * @param bitmap 要选择的图片
     */
    public static Bitmap rotateBitmap(String filePath, Bitmap bitmap) {
        int degree = readPictureDegree(filePath);
        if (degree > 0) {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            // 创建新的图片
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

}
