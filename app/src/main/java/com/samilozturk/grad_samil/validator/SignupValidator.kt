package com.samilozturk.grad_samil.validator

import android.util.Patterns
import com.samilozturk.grad_samil.data.SignupCredentials

class SignupValidator : Validator<SignupCredentials> {

    override fun validate(args: SignupCredentials) {
        if (args.firstName.length < 2) {
            error("İsim en az 2 karakter olmalıdır")
        }
        if (args.lastName.length < 2) {
            error("Soyisim en az 2 karakter olmalıdır")
        }
        if (args.entYear.toIntOrNull() == null) {
            error("Giriş yılı sayı olmalıdır")
        }
        if (args.entYear.length != 4) {
            error("Giriş yılı geçersiz")
        }
        if (args.entYear.toInt() < 2000 || args.entYear.toInt() > 2023) {
            error("Giriş yılı 2000 ile 2023 arasında olmalıdır")
        }
        if (args.gradYear.toIntOrNull() == null) {
            error("Mezuniyet yılı sayı olmalıdır")
        }
        if (args.gradYear.length != 4) {
            error("Mezuniyet yılı geçersiz")
        }
        if (args.gradYear.toInt() < 2000 || args.gradYear.toInt() > 2023) {
            error("Mezuniyet yılı 2000 ile 2023 arasında olmalıdır")
        }
        if (args.gradYear.toInt() < args.entYear.toInt()) {
            error("Mezuniyet yılı giriş yılından küçük olamaz")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Geçersiz email")
        }
        if (args.password.length < 6) {
            error("Şifre en az 6 karakter olmalıdır")
        }
        if (!args.password.contains(Regex("[0-9]"))) {
            error("Şifre en az 1 rakam içermelidir")
        }
        if (!args.password.contains(Regex("[a-z]"))) {
            error("Şifre en az 1 küçük harf içermelidir")
        }
        if (!args.password.contains(Regex("[A-Z]"))) {
            error("Şifre en az 1 büyük harf içermelidir")
        }
    }

}