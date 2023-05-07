package com.samilozturk.grad_samil.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class Authenticator {

    private val auth = Firebase.auth

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun login(email: String, password: String): FirebaseUser {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                error("Giriş yapılamadı")
            }
            return result.user!!
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException,
                is FirebaseAuthInvalidCredentialsException,
                -> {
                    error("Email veya şifre hatalı")
                }
                else -> {
                    error("Giriş yapılırken bir hata oluştu")
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun signup(email: String, password: String): FirebaseUser {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                error("Kayıt yapılamadı")
            }
            return result.user!!
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException,
                is FirebaseAuthInvalidCredentialsException,
                -> {
                    error("Email veya şifre hatalı")
                }
                is FirebaseAuthUserCollisionException -> {
                    error("Bu e-posta adresi zaten kullanılıyor")
                }
                else -> {
                    error("Kayıt olunurken bir hata oluştu")
                }
            }
        }
    }

    suspend fun sendResetPasswordEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    error("Bu e-posta adresi ile kayıtlı bir kullanıcı bulunamadı")
                }
                else -> {
                    error("Şifre sıfırlanırken bir hata oluştu")
                }
            }
        }
    }

}