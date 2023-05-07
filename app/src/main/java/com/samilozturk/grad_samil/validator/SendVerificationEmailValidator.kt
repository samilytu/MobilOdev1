package com.samilozturk.grad_samil.validator

import android.util.Patterns
import com.samilozturk.grad_samil.data.SendVerificationEmailCredentials

class SendVerificationEmailValidator : Validator<SendVerificationEmailCredentials> {

    override fun validate(args: SendVerificationEmailCredentials) {
        if(args.email.isEmpty()) {
            error("E-posta boş olamaz")
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Geçersiz e-posta")
        }
    }

}