package com.samilozturk.grad_samil.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.samilozturk.grad_samil.data.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Announcement(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val content: String = "",
    @ServerTimestamp
    val createdAt: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp = Timestamp.now(),
    @get:PropertyName("imagePath")
    val imageUrl: String = "",
    @get:Exclude
    val author: User = User(),
) : Parcelable