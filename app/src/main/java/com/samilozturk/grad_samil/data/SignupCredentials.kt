package com.samilozturk.grad_samil.data
import android.net.Uri

data class SignupCredentials(
    val imageUri: Uri?,
    val firstName: String,
    val lastName: String,
    val entYear: String,
    val gradYear: String,
    val email: String,
    val password: String,
)