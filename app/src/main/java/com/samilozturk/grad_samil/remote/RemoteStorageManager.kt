package com.samilozturk.grad_samil.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class RemoteStorageManager {

    private val storage = Firebase.storage

    suspend fun uploadImage(imageUri: Uri, uriFileName: String): String {
        try {
            return uploadFile(imageUri, uriFileName, "images")
        } catch (e: Exception) {
            e.printStackTrace()
            error("Resim yüklenirken bir hata oluştu.")
        }
    }

    suspend fun uploadVideo(videoUri: Uri, uriFileName: String): String {
        try {
            return uploadFile(videoUri, uriFileName, "videos")
        } catch (e: Exception) {
            e.printStackTrace()
            error("Video yüklenirken bir hata oluştu.")
        }
    }

    suspend fun uploadThumbnail(thumbnailUri: Uri, uriFileName: String): String {
        try {
            return uploadFile(thumbnailUri, uriFileName, "thumbnails")
        } catch (e: Exception) {
            e.printStackTrace()
            error("Thumbnail yüklenirken bir hata oluştu.")
        }
    }

    private suspend fun uploadFile(imageUri: Uri, uriFileName: String, path: String): String {
        val storageRef = storage.reference
        // insert timestamp to uri name before extension
        val uriName = uriFileName.substringBeforeLast(".")
        val uriExtension = uriFileName.substringAfterLast(".")
        val fileName = "$uriName-${System.currentTimeMillis()}.$uriExtension"
        val imageRef = storageRef.child("${path}/$fileName")
        val snapshot = imageRef.putFile(imageUri).await()
        return snapshot.metadata?.path.toString()
    }

}