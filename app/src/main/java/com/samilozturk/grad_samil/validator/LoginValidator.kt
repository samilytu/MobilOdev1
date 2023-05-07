package com.samilozturk.grad_samil.validator

import android.util.Patterns
import com.samilozturk.grad_samil.data.LoginCredentials

class LoginValidator : Validator<LoginCredentials> {

    override fun validate(args: LoginCredentials) {
        if(args.email.isEmpty()) {
            error("E-posta boş olamaz")
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Geçersiz e-posta")
        }
        if(args.password.isEmpty()) {
            error("Şifre boş olamaz")
        }
    }

}