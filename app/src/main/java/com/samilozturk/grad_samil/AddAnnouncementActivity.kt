package com.samilozturk.grad_samil

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.samilozturk.grad_samil.data.AddAnnouncementData
import com.samilozturk.grad_samil.data.Announcement
import com.samilozturk.grad_samil.databinding.ActivityAddAnnouncementBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.remote.DbManager
import com.samilozturk.grad_samil.remote.RemoteStorageManager
import com.samilozturk.grad_samil.util.getFileName
import com.samilozturk.grad_samil.util.snackbar
import com.samilozturk.grad_samil.util.toast
import com.samilozturk.grad_samil.validator.AddAnnouncementValidator
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddAnnouncementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnouncementBinding
    private val remoteStorageManager = RemoteStorageManager()
    private val dbManager = DbManager()
    private val validator = AddAnnouncementValidator()
    private val authenticator = Authenticator()
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }
    private var title: String = ""
    private var content: String = ""
    private var imageUri: Uri? = null
    private var expirationDate: Date? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("AddAnnouncementActivity", "onCreate: $uri")
                imageUri = uri
                binding.imageView.setImageURI(uri)
                binding.buttonPickImage.text = "Resmi Değiştir"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // change home button icon
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        // change home icon tint
        // finish activity when home button clicked

        binding.buttonPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editTextTitle.doAfterTextChanged {
            title = it.toString()
        }

        binding.editTextContent.doAfterTextChanged {
            content = it.toString()
        }

        binding.editTextPickDate.setOnClickListener {
            // show date picker
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Duyuru Son Tarihini Seçiniz")
            .setSelection(expirationDate?.time ?: MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                    .build()
            )
            .build()

        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            expirationDate = Date(it)
            binding.editTextPickDate.setText(datePicker.headerText)
        }
    }

    private fun isAnyFieldFilled(): Boolean {
        return title.isNotEmpty() || content.isNotEmpty() || imageUri != null || expirationDate != null

    }

    override fun onSupportNavigateUp(): Boolean {
        if (isAnyFieldFilled()) {
            // show dialog
            showAlertDialog()
            return false
        } else {
            finish()
            return true
        }
    }

    private fun showAlertDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Uyarı")
            .setMessage("Yaptığınız değişiklikler kaydedilmeyecek. Çıkmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                finish()
            }
            .setNegativeButton("İptal", null)
            .create()

        dialog.show()
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_announcement_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_save -> {
                // save announcement
                try {
                    val data = AddAnnouncementData(title, content, imageUri, expirationDate)
                    validator.validate(data)
                    saveAnnouncement()
                } catch (e: Exception) {
                    snackbar(e.message.toString(), isError = true)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun saveAnnouncement() {
        lifecycleScope.launch {
            progressDialog.setMessage("Duyuru oluşturuluyor...")
            progressDialog.show()
            try {
                val imagePath = remoteStorageManager.uploadImage(
                    imageUri = imageUri!!,
                    uriFileName = imageUri!!.getFileName(contentResolver)!!
                )
                val announcement = Announcement(
                    title = title,
                    content = content,
                    expiresAt = Timestamp(
                        Calendar.getInstance().apply {
                            time = expirationDate!!
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }.time
                    ),
                    imageUrl = imagePath
                )
                dbManager.createAnnouncement(announcement, authenticator.getCurrentUser()!!.uid)
                toast("Duyuru başarıyla oluşturuldu")
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            } finally {
                progressDialog.dismiss()
            }
        }
    }
}