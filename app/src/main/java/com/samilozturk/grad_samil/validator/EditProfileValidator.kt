package com.samilozturk.grad_samil.validator

import com.samilozturk.grad_samil.data.EditProfileData

class EditProfileValidator : Validator<EditProfileData> {

    override fun validate(args: EditProfileData) {
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
        // workCompany, workCountry, and workCity are optional
        // they must be blank together or filled together
        if (args.workCompany.isNotBlank() && args.workCountry.isNotBlank() && args.workCity.isNotBlank()) {
            if (args.workCompany.length < 2) {
                error("İş yeri en az 2 karakter olmalıdır")
            }
            if (args.workCountry.length < 2) {
                error("İş ülkesi en az 2 karakter olmalıdır")
            }
            if (args.workCity.length < 2) {
                error("İş şehri en az 2 karakter olmalıdır")
            }
        } else if (args.workCompany.isNotBlank() || args.workCountry.isNotBlank() || args.workCity.isNotBlank()) {
            error("İş bilgileri eksik")
        }

        val userNameRegex =
            "^(?!.*\\.{2,})(?!.*\\.$)(?!.*_$)(?!.*\\.-)[a-zA-Z0-9_.]{1,30}\$".toRegex()
        if (args.socialMediaInstagram.isNotBlank() && !args.socialMediaInstagram.matches(userNameRegex)) {
            error("Geçersiz Instagram kullanıcı adı")
        }
        if (args.socialMediaTwitter.isNotBlank() && !args.socialMediaTwitter.matches(userNameRegex)) {
            error("Geçersiz Twitter kullanıcı adı")
        }
        if (args.socialMediaLinkedin.isNotBlank() && !args.socialMediaLinkedin.matches(userNameRegex)) {
            error("Geçersiz Linkedin kullanıcı adı")
        }
        if (args.socialMediaFacebook.isNotBlank() && !args.socialMediaFacebook.matches(userNameRegex)) {
            error("Geçersiz Facebook kullanıcı adı")
        }
        // phone is must be blank or in format +90 5XX XXX XXXX
        val phoneRegex = "^\\+90 5[0-9]{2} [0-9]{3} [0-9]{4}$".toRegex()
        if (args.phone.isNotBlank() && !args.phone.matches(phoneRegex)) {
            error("Telefon numarası geçersiz")
        }
    }

}