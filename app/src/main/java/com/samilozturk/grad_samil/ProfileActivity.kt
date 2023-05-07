package com.samilozturk.grad_samil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.samilozturk.grad_samil.data.User
import com.samilozturk.grad_samil.databinding.ActivityProfileBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.remote.LocalStorageManager
import com.samilozturk.grad_samil.util.toast

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val authenticator = Authenticator()
    private val remoteStorageManager by lazy { LocalStorageManager(this) }
    private var isEditable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isEditable = intent.getBooleanExtra("isEditable", false)

        supportActionBar?.title = if (isEditable) "Profilim" else "Mezun Profili"

        val user = intent.getParcelableExtra<User>("user")!!
        populate(user)
    }

    private fun populate(user: User) {
        if (isEditable) {
            binding.buttonEditProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("user", user)
                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
            }
            binding.buttonLogout.setOnClickListener {
                onClickLogout()
            }
        } else {
            binding.buttonEditProfile.isVisible = false
            binding.buttonLogout.isVisible = false
        }

        Glide.with(this).load(user.imageUrl).placeholder(R.drawable.image_placeholder)
            .into(binding.shapeableImageView)
        binding.textViewName.text = "${user.firstName} ${user.lastName}"
        binding.textViewYears.text = "${user.entYear} - ${user.gradYear}"

        if (user.education != null) {
            binding.textViewEducation.text = user.education.toString()
            binding.textViewEducationTitle.isVisible = true
            binding.textViewEducation.isVisible = true
        } else {
            binding.textViewEducationTitle.isVisible = false
            binding.textViewEducation.isVisible = false
        }

        if (user.work != null) {
            binding.textViewWork.text =
                "${user.work.company} - ${user.work?.city} / ${user.work?.country}"
            binding.textViewWorkTitle.isVisible = true
            binding.textViewWork.isVisible = true
        } else {
            binding.textViewWorkTitle.isVisible = false
            binding.textViewWork.isVisible = false
        }

        if (user.socialMedia.facebook != null) {
            binding.textViewSocialMediaFacebook.text = "@${user.socialMedia.facebook}"
            binding.layoutSocialMediaFacebook.setOnClickListener {
                openSocialMedia("https://www.facebook.com", user.socialMedia.facebook)
            }
            binding.layoutSocialMediaFacebook.isVisible = true
        } else {
            binding.layoutSocialMediaFacebook.isVisible = false
        }

        if (user.socialMedia.instagram != null) {
            binding.textViewSocialMediaInstagram.text = "@${user.socialMedia.instagram}"
            binding.layoutSocialMediaInstagram.setOnClickListener {
                openSocialMedia("https://www.instagram.com", user.socialMedia.instagram)
            }
            binding.layoutSocialMediaInstagram.isVisible = true
        } else {
            binding.layoutSocialMediaInstagram.isVisible = false
        }

        if (user.socialMedia.twitter != null) {
            binding.textViewSocialMediaTwitter.text = "@${user.socialMedia.twitter}"
            binding.layoutSocialMediaTwitter.setOnClickListener {
                openSocialMedia("https://www.twitter.com", user.socialMedia.twitter)
            }
            binding.layoutSocialMediaTwitter.isVisible = true
        } else {
            binding.layoutSocialMediaTwitter.isVisible = false
        }

        if (user.socialMedia.linkedin != null) {
            binding.textViewSocialMediaLinkedin.text = "@${user.socialMedia.linkedin}"
            binding.layoutSocialMediaLinkedin.setOnClickListener {
                openSocialMedia("https://www.linkedin.com/in", user.socialMedia.linkedin)
            }
            binding.layoutSocialMediaLinkedin.isVisible = true
        } else {
            binding.layoutSocialMediaLinkedin.isVisible = false
        }

        binding.textViewContactEmail.text = user.email
        binding.textViewContactEmail.setOnClickListener {
            openEmail(user.email)
        }

        if (user.phone != null) {
            binding.textViewContactPhone.text = user.phone
            binding.textViewContactPhone.setOnClickListener {
                openWhatsapp(user.phone)
            }
            binding.textViewContactPhone.isVisible = true
        } else {
            binding.textViewContactPhone.isVisible = false
        }

        binding.textViewSocialMediaTitle.isVisible =
            user.socialMedia.facebook != null || user.socialMedia.instagram != null || user.socialMedia.twitter != null || user.socialMedia.linkedin != null
    }

    private fun openEmail(email: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        startActivity(Intent.createChooser(intent, "E-posta gönder"))
    }

    private fun openWhatsapp(phone: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val phone = phone.replace(" ", "").replace("+", "")
        intent.data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$phone")
        startActivity(intent)
    }

    private fun openSocialMedia(website: String, username: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse("$website/$username")
        startActivity(intent)
    }

    private fun onClickLogout() {
        authenticator.logout()
        remoteStorageManager.clearUser()
        toast("Çıkış yapıldı")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            val user = data?.getParcelableExtra<User>("user")!!
            populate(user)
        }
    }
    
    companion object {
        const val REQUEST_CODE_EDIT_PROFILE = 1
    }
}