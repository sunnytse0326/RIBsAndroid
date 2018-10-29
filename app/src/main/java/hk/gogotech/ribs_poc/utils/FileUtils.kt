package hk.gogotech.ribs_poc.utils

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.*
import java.text.DecimalFormat
import java.util.*

object FileUtils {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getFileInfo(context: Context, uri: Uri): Hashtable<String, Any>? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    val value = Hashtable<String, Any>()
                    value["path"] = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    value["size"] = File(value["path"] as String).length().toInt()
                    value["mime"] = "application/octet-stream"

                    return value
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            if (isNewGooglePhotosUri(uri)) {
                val value = getDataColumn(context, uri, null, null)
                val bitmap: Bitmap
                try {
                    val input = context.contentResolver.openInputStream(uri)
                    bitmap = BitmapFactory.decodeStream(input)
                    val file = File.createTempFile("sendbird", ".jpg")
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, BufferedOutputStream(FileOutputStream(file)))
                    value!!["path"] = file.absolutePath
                    value["size"] = file.length().toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return value
            } else {
                return getDataColumn(context, uri, null, null)
            }
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            val value = Hashtable<String, Any>()
            value["path"] = uri.path!!
            value["size"] = File(value["path"] as String).length().toInt()
            value["mime"] = "application/octet-stream"

            return value
        }// File
        // MediaStore (and general)

        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): Hashtable<String, Any>? {

        var cursor: Cursor? = null
        val COLUMN_DATA = MediaStore.MediaColumns.DATA
        var COLUMN_MIME = MediaStore.MediaColumns.MIME_TYPE
        val COLUMN_SIZE = MediaStore.MediaColumns.SIZE

        val projection = arrayOf(COLUMN_DATA, COLUMN_MIME, COLUMN_SIZE)

        try {
            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            } catch (e: IllegalArgumentException) {
                COLUMN_MIME = "mimetype" // DownloadProvider.sAppReadableColumnsArray.COLUMN_MIME_TYPE
                projection[1] = COLUMN_MIME
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            }

            if (cursor != null && cursor.moveToFirst()) {
                var column_index = cursor.getColumnIndexOrThrow(COLUMN_DATA)
                var path: String? = cursor.getString(column_index)

                column_index = cursor.getColumnIndexOrThrow(COLUMN_MIME)
                var mime: String? = cursor.getString(column_index)

                column_index = cursor.getColumnIndexOrThrow(COLUMN_SIZE)
                val size = cursor.getInt(column_index)

                val value = Hashtable<String, Any>()
                if (path == null) path = ""
                if (mime == null) mime = "application/octet-stream"

                value["path"] = path
                value["mime"] = mime
                value["size"] = size

                return value
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isNewGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.contentprovider" == uri.authority
    }

    /**
     * Downloads a file using DownloadManager.
     */
    fun downloadFile(context: Context, url: String, fileName: String) {
        val downloadRequest = DownloadManager.Request(Uri.parse(url))
        downloadRequest.setTitle(fileName)

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            downloadRequest.allowScanningByMediaScanner()
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(downloadRequest)
    }


    /**
     * Converts byte value to String.
     */
    fun toReadableFileSize(size: Long): String {
        if (size <= 0) return "0KB"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    @Throws(IOException::class)
    fun saveToFile(file: File, data: String) {
        val tempFile = File.createTempFile("sendbird", "temp")
        val fos = FileOutputStream(tempFile)
        fos.write(data.toByteArray())
        fos.close()

        if (!tempFile.renameTo(file)) {
            throw IOException("Error to rename file to " + file.absolutePath)
        }
    }

    @Throws(IOException::class)
    fun loadFromFile(file: File): String {
        val stream = FileInputStream(file)
        val reader = BufferedReader(InputStreamReader(stream))
        val builder = StringBuilder()
        val buffer = CharArray(8192)
        var read: Int
        read = reader.read(buffer, 0, buffer.size)
        while ((read) > 0) {
            builder.append(buffer, 0, read)
            read = reader.read(buffer, 0, buffer.size)
        }
        return builder.toString()
    }

    fun deleteFile(file: File?) {
        if (file != null && file.exists()) {
            file.delete()
        }
    }
}// Prevent instantiation
