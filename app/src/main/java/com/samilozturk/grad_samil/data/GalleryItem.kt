package com.samilozturk.grad_samil.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryItem(
    @DocumentId
    val id: String = "",
    val title: String = "",
    @get:PropertyName("mediaPath")
    val mediaUrl: String = "",
    @get:PropertyName("thumbnailPath")
    val thumbnailUrl: String = "",
    @get:PropertyName("isVideo")
    val isVideo: Boolean = false,
    @get:Exclude
    val author: User = User(),
    @ServerTimestamp
    val createdAt: Timestamp = Timestamp.now(),
) : Parcelable