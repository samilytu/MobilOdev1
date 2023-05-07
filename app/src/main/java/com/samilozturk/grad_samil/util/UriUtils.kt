package com.samilozturk.grad_samil.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlin.math.roundToInt


@SuppressLint("Range")
fun Uri.getFileName(contentResolver: ContentResolver): String? {
    val cursor = contentResolver.query(this, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            return displayName!!
        }
    }
    return null
}

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, this))
    else
        MediaStore.Images.Media.getBitmap(contentResolver, this)

fun Uri.getThumbnailBitmap(contentResolver: ContentResolver): Bitmap {
    val bitmap = toBitmap(contentResolver)
    return bitmap.getThumbnailBitmap()
}

fun Bitmap.getThumbnailBitmap(): Bitmap {
    // if width > height, width = 200, height = 200 * height / width
    // if height > width, height = 200, width = 200 * width / height
    val maxFrameSize = 200
    val width = min(maxFrameSize, (maxFrameSize * this.width.toFloat() / this.height.toFloat()).roundToInt())
    val height = min(maxFrameSize, (maxFrameSize * this.height.toFloat() / this.width.toFloat()).roundToInt())
    return ThumbnailUtils.extractThumbnail(this, width, height)
}

fun Bitmap.toUri(contentResolver: ContentResolver): Uri {
    val bytes = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(contentResolver, this, "Bitmap${System.currentTimeMillis()}", null)
    return Uri.parse(path)
}