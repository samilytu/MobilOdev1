package com.samilozturk.grad_samil.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openImage(imageUrl: String) {
    val imageUri = Uri.parse(imageUrl)
    openImage(imageUri)
}

fun Context.openImage(imageUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(imageUri, "image/*")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
    startActivity(intent)
    toast("Resim görüntüleniyor")
}

fun Context.openVideo(videoUrl: String) {
    val videoUri = Uri.parse(videoUrl)
    openVideo(videoUri)
}

fun Context.openVideo(videoUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(videoUri, "video/*")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //DO NOT FORGET THIS EVER
    startActivity(intent)
    toast("Video oynatılıyor")
}