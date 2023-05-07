package com.samilozturk.grad_samil.data

data class EditProfileData(
    val firstName: String,
    val lastName: String,
    val entYear: String,
    val gradYear: String,
    val phone: String,
    val education: Education?,
    val workCompany: String,
    val workCountry: String,
    val workCity: String,
    val socialMediaInstagram: String,
    val socialMediaTwitter: String,
    val socialMediaLinkedin: String,
    val socialMediaFacebook: String,
)