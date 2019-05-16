package com.yuruiyin.richeditor.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.yuruiyin.richeditor.enumtype.FileTypeEnum;

import java.io.File;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-13
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    public static String getFileType(String path) {
        if (TextUtils.isEmpty(path)) {
            return FileTypeEnum.STATIC_IMAGE;
        }

        if (path.endsWith(".mp4") || path.endsWith(".avi")
            || path.endsWith(".3gpp") || path.endsWith(".3gp") || path.startsWith(".mov")) {
            return FileTypeEnum.VIDEO;
        } else if (path.endsWith(".PNG") || path.endsWith(".png") || path.endsWith(".jpeg")
            || path.endsWith(".jpg")
            || path.endsWith(".webp") || path.endsWith(".WEBP") || path.endsWith(".JPEG")
            || path.endsWith(".bmp")) {
            return FileTypeEnum.STATIC_IMAGE;
        } else if (path.endsWith(".gif") || path.endsWith(".GIF")) {
            return FileTypeEnum.GIF;
        } else if (path.endsWith(".mp3") || path.endsWith(".amr")
            || path.endsWith(".aac") || path.endsWith(".war")
            || path.endsWith(".flac") || path.endsWith(".lamr")) {
            return FileTypeEnum.AUDIO;
        }

        return FileTypeEnum.STATIC_IMAGE;
    }

    public static String getFileType(File file) {
        if (file == null) {
            Log.e(TAG, "file is null");
            return FileTypeEnum.STATIC_IMAGE;
        }

        return getFileType(file.getAbsolutePath());
    }

    public static String getFileType(Context context, Uri uri) {
        if (uri == null) {
            Log.e(TAG, "uri is null");
            return FileTypeEnum.STATIC_IMAGE;
        }

        String path = getFileRealPath(context, uri);
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "path is null");
            return FileTypeEnum.STATIC_IMAGE;
        }

        return getFileType(path);
    }

    /**
     * 获取文件真实路径
     *
     * @param context 上下文
     * @param uri     文件uri
     * @return 文件真实路径
     */
    public static String getFileRealPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= 19) { // api >= 19
                return getRealPathFromUriAboveApi19(context, uri);
            } else { // api < 19
                return getRealPathFromUriBelowAPI19(context, uri);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }


    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
