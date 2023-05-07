package com.samilozturk.grad_samil.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class User(
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val entYear: Int = 0,
    val gradYear: Int = 0,
    val email: String = "",
    @get:PropertyName("imagePath")
    val imageUrl: String? = null,
    val phone: String? = null,
    val education: Education? = null,
    val work: Work? = null,
    val socialMedia: SocialMedia = SocialMedia(),
) : Parcelable {
    fun toJson(): String {
        return Json.encodeToString(this)
    }
    companion object {
        fun fromJson(json: String): User {
            return Json.decodeFromString(json)
        }
    }
}

enum class Education {
    UNIVERSITY {
        override fun toString() = "Lisans"
    },
    MASTER {
        override fun toString() = "Yüksek Lisans"
    },
    DOCTORATE {
        override fun toString() = "Doktora"
    },
}

@Serializable
@Parcelize
data class Work(
    val country: String = "",
    val city: String = "",
    val company: String = "",
) : Parcelable

@Serializable
@Parcelize
data class SocialMedia(
    val instagram: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null,
    val facebook: String? = null,
) : Parcelable