package com.yuruiyin.richeditor.sample.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore

/**
 * Title: 图片相关工具类
 * Description: 如获取图片宽高、获取图片真实路径等方法
 *
 * @author yuruiyin
 * @version 2019-05-08
 */
class ImageUtil {

    companion object {

        /**
         * 通过本地图片路径获取图片的宽高
         *
         * @param path 图片路径
         * @return
         */
        fun getImageWidthHeight(path: String): IntArray {
            val options = BitmapFactory.Options()

            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */
            options.inJustDecodeBounds = true
            val bitmap = BitmapFactory.decodeFile(path, options) // 此时返回的bitmap为null
            // options.outHeight为原始图片的高
            return intArrayOf(options.outWidth, options.outHeight)
        }

        /**
         * 根据Uri获取图片的绝对路径
         *
         * @param context 上下文对象
         * @param uri     图片的Uri
         * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
         */
        fun getRealPathFromUri(context: Context, uri: Uri): String? {
            val sdkVersion = Build.VERSION.SDK_INT
            return if (sdkVersion >= 19) {
                getRealPathFromUriAboveApi19(context, uri)
            } else {
                getRealPathFromUriBelowAPI19(context, uri)
            }
        }

        /**
         * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
         *
         * @param context 上下文对象
         * @param uri     图片的Uri
         * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
         */
        private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
            return getDataColumn(context, uri, null, null)
        }

        /**
         * 适配api19及以上,根据uri获取图片的绝对路径
         *
         * @param context 上下文对象
         * @param uri     图片的Uri
         * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
         */
        @SuppressLint("NewApi")
        private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
            var filePath: String? = null
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // 如果是document类型的 uri, 则通过document id来进行处理
                val documentId = DocumentsContract.getDocumentId(uri)
                if (isMediaDocument(uri)) { // MediaProvider
                    // 使用':'分割
                    val id = documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

                    val selection = MediaStore.Images.Media._ID + "=?"
                    val selectionArgs = arrayOf(id)
                    filePath =
                        getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs)
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(documentId)
                    )
                    filePath = getDataColumn(context, contentUri, null, null)
                }
            } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                // 如果是 content 类型的 Uri
                filePath = getDataColumn(context, uri, null, null)
            } else if ("file" == uri.scheme) {
                // 如果是 file 类型的 Uri,直接获取图片对应的路径
                filePath = uri.path
            }
            return filePath
        }

        /**
         * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
         *
         * @return
         */
        private fun getDataColumn(
            context: Context,
            uri: Uri,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var path: String? = null

            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                    path = cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                if (cursor != null) {
                    cursor.close()
                }
            }

            return path
        }

        /**
         * @param uri the Uri to check
         * @return Whether the Uri authority is MediaProvider
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }


        /**
         * @param uri the Uri to check
         * @return Whether the Uri authority is DownloadsProvider
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

    }

}