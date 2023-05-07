package com.samilozturk.grad_samil.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.samilozturk.grad_samil.data.Announcement
import com.samilozturk.grad_samil.data.GalleryItem
import com.samilozturk.grad_samil.data.User
import kotlinx.coroutines.tasks.await

class DbManager {

    private val db = Firebase.firestore

    suspend fun createUser(user: User) {
        db.collection("users")
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun getUser(id: String): User {
        try {
            val doc = db.collection("users")
                .document(id)
                .get()
                .await()
            val imagePath = doc["imagePath"] as String?
            val imageUrl = imagePath?.let {
                Firebase.storage.reference.child(it).downloadUrl.await()
                    .toString()
            }
            return doc.toObject<User>()!!.copy(imageUrl = imageUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            error("Kullanıcı bulunamadı")
        }
    }

    suspend fun updateUser(userId: String, user: User) {
        try {
            db.collection("users")
                .document(userId)
                .set(user)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            error("Kullanıcı güncellenemedi")
        }
    }

    suspend fun getAllUsers(userId: String): List<User> {
        try {
            val result = db.collection("users")
                .whereNotEqualTo(FieldPath.documentId(), userId)
                .get()
                .await()
            return result.documents.map { doc ->
                val imagePath = doc["imagePath"] as String?
                val imageUrl = imagePath?.let {
                    Firebase.storage.reference.child(it).downloadUrl.await()
                        .toString()
                }
                doc.toObject<User>()!!.copy(imageUrl = imageUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error("Kullanıcılar getirilemedi")
        }
    }

    suspend fun createAnnouncement(announcement: Announcement, userId: String) {
        try {
            val docRef = db.collection("announcements")
                .add(announcement)
                .await()
            // set authorRef field
            db.collection("announcements")
                .document(docRef.id)
                .update("authorRef", db.collection("users").document(userId))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            error("Duyuru oluşturulamadı")
        }
    }

    suspend fun getAllAnnouncements(): List<Announcement> {
        try {
            val result = db.collection("announcements")
                .whereGreaterThan("expiresAt", Timestamp.now())
                .orderBy("expiresAt")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            // join authors to announcements
            // author field is a reference to users collection
            return result.documents.map { doc ->
                val authorDocRef = doc["authorRef"] as DocumentReference
                val author = authorDocRef.get().await().toObject<User>()!!
                val imageUrl =
                    Firebase.storage.reference.child(doc["imagePath"] as String).downloadUrl.await()
                        .toString()
                val announcement = doc.toObject<Announcement>()!!
                announcement.copy(
                    author = author,
                    imageUrl = imageUrl
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error("Duyurular getirilemedi")
        }
    }

    suspend fun createGalleryItem(galleryItem: GalleryItem, userId: String) {
        try {
            val docRef = db.collection("gallery")
                .add(galleryItem)
                .await()
            // set authorRef field
            db.collection("gallery")
                .document(docRef.id)
                .update("authorRef", db.collection("users").document(userId))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            error("Galeri öğesi oluşturulamadı")
        }
    }

    suspend fun getAllGalleryItems(): List<GalleryItem> {
        try {
            val result = db.collection("gallery")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            // join authors to gallery items
            // author field is a reference to users collection
            return result.documents.map { doc ->
                val authorDocRef = doc["authorRef"] as DocumentReference
                val author = authorDocRef.get().await().toObject<User>()!!
                val mediaUrl =
                    Firebase.storage.reference.child(doc["mediaPath"] as String).downloadUrl.await()
                        .toString()
                val thumbnailUrl =
                    Firebase.storage.reference.child(doc["thumbnailPath"] as String).downloadUrl.await()
                        .toString()
                val galleryItem = doc.toObject<GalleryItem>()!!
                galleryItem.copy(
                    author = author,
                    mediaUrl = mediaUrl,
                    thumbnailUrl = thumbnailUrl
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error("Galeri öğeleri getirilemedi")
        }
    }

    suspend fun deleteGalleryItem(galleryItem: GalleryItem) {
        try {
            db.collection("gallery")
                .document(galleryItem.id)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            error("Galeri öğesi silinemedi")
        }
    }

}