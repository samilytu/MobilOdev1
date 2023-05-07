package com.samilozturk.grad_samil

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.samilozturk.grad_samil.data.EditProfileData
import com.samilozturk.grad_samil.data.Education
import com.samilozturk.grad_samil.data.SocialMedia
import com.samilozturk.grad_samil.data.User
import com.samilozturk.grad_samil.data.Work
import com.samilozturk.grad_samil.databinding.ActivityEditProfileBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.remote.DbManager
import com.samilozturk.grad_samil.remote.LocalStorageManager
import com.samilozturk.grad_samil.remote.RemoteStorageManager
import com.samilozturk.grad_samil.util.getFileName
import com.samilozturk.grad_samil.util.snackbar
import com.samilozturk.grad_samil.util.toUri
import com.samilozturk.grad_samil.validator.EditProfileValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val validator = EditProfileValidator()
    private val remoteStorageManager = RemoteStorageManager()
    private val dbManager = DbManager()
    private val authenticator = Authenticator()
    private val localStorageManager by lazy { LocalStorageManager(this) }
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    private lateinit var user: User
    private var imageUri: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                imageUri = uri
                binding.imageView.setImageURI(uri)
                binding.buttonPickImage.text = "Resmi Değiştir"
            }
        }

    private val educationValues = listOf("Seçilmedi") + Education.values().map { it.toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // change home button icon
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        user = intent.getParcelableExtra("user")!!

        user.imageUrl?.let {
            Glide.with(this)
                .load(it)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        imageUri = bitmap.toUri(contentResolver)
                        return false
                    }

                })
                .into(binding.imageView)
            binding.buttonPickImage.text = "Resmi Değiştir"
        }

        binding.buttonPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editTextFirstName.setText(user.firstName)
        binding.editTextLastName.setText(user.lastName)
        binding.editTextEntranceYear.setText(user.entYear.toString())
        binding.editTextGradYear.setText(user.gradYear.toString())
        binding.spinnerEducation.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, educationValues)
        user.education?.let {
            binding.spinnerEducation.setSelection(educationValues.indexOf(it.toString()))
        }
        user.work?.let {
            binding.editTextWorkCompany.setText(it.company)
            binding.editTextWorkCountry.setText(it.country)
            binding.editTextWorkCity.setText(it.city)
        }
        binding.editTextFacebook.setText(user.socialMedia.facebook)
        binding.editTextInstagram.setText(user.socialMedia.instagram)
        binding.editTextTwitter.setText(user.socialMedia.twitter)
        binding.editTextLinkedin.setText(user.socialMedia.linkedin)
        binding.editTextEmail.setText(user.email)
        binding.editTextPhone.setText(user.phone)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_save -> {
                // save announcement
                try {
                    val firstName = binding.editTextFirstName.text.toString()
                    val lastName = binding.editTextLastName.text.toString()
                    val entYear = binding.editTextEntranceYear.text.toString()
                    val gradYear = binding.editTextGradYear.text.toString()
                    val education =
                        binding.spinnerEducation.selectedItemPosition.let { if (it == 0) null else Education.values()[it - 1] }
                    val workCompany = binding.editTextWorkCompany.text.toString()
                    val workCountry = binding.editTextWorkCountry.text.toString()
                    val workCity = binding.editTextWorkCity.text.toString()
                    val facebook = binding.editTextFacebook.text.toString()
                    val instagram = binding.editTextInstagram.text.toString()
                    val twitter = binding.editTextTwitter.text.toString()
                    val linkedin = binding.editTextLinkedin.text.toString()
                    val phone = binding.editTextPhone.text.toString()

                    val data = EditProfileData(
                        firstName,
                        lastName,
                        entYear,
                        gradYear,
                        phone,
                        education,
                        workCompany,
                        workCountry,
                        workCity,
                        instagram,
                        twitter,
                        linkedin,
                        facebook
                    )
                    validator.validate(data)
                    saveProfile(data)
                } catch (e: Exception) {
                    snackbar(e.message.toString(), isError = true)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun saveProfile(data: EditProfileData) {
        lifecycleScope.launch {
            try {
                progressDialog.setMessage("Profil güncelleniyor...")
                progressDialog.show()
                val newImagePath = imageUri?.let {
                    remoteStorageManager.uploadImage(it, it.getFileName(contentResolver)!!)
                }
                val newUser = User(
                    firstName = data.firstName,
                    lastName = data.lastName,
                    entYear = data.entYear.toInt(),
                    gradYear = data.gradYear.toInt(),
                    email = user.email,
                    imageUrl = newImagePath,
                    phone = data.phone.takeIf { it.isNotBlank() },
                    education = data.education,
                    work = Work(
                        company = data.workCompany,
                        country = data.workCountry,
                        city = data.workCity
                    ).takeIf { it.company.isNotBlank() && it.country.isNotBlank() && it.city.isNotBlank() },
                    socialMedia = SocialMedia(
                        instagram = data.socialMediaInstagram.takeIf { it.isNotBlank() },
                        twitter = data.socialMediaTwitter.takeIf { it.isNotBlank() },
                        linkedin = data.socialMediaLinkedin.takeIf { it.isNotBlank() },
                        facebook = data.socialMediaFacebook.takeIf { it.isNotBlank() }
                    )
                )
                dbManager.updateUser(authenticator.getCurrentUser()!!.uid, newUser)
                val newImageUrl = newImagePath?.let {
                    Firebase.storage.getReference(it).downloadUrl.await()
                }
                val newUserFinal = newUser.copy(imageUrl = newImageUrl?.toString())
                localStorageManager.saveUser(newUserFinal)
                val intent = Intent().apply { putExtra("user", newUserFinal) }
                setResult(Activity.RESULT_OK, intent)
                snackbar("Profil güncellendi")
                finish()
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

}