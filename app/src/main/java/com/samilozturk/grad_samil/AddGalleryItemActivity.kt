package com.samilozturk.grad_samil

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.samilozturk.grad_samil.data.AddGalleryItemData
import com.samilozturk.grad_samil.data.GalleryItem
import com.samilozturk.grad_samil.databinding.ActivityAddGalleryItemBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.remote.DbManager
import com.samilozturk.grad_samil.remote.RemoteStorageManager
import com.samilozturk.grad_samil.util.getFileName
import com.samilozturk.grad_samil.util.getThumbnailBitmap
import com.samilozturk.grad_samil.util.openImage
import com.samilozturk.grad_samil.util.openVideo
import com.samilozturk.grad_samil.util.snackbar
import com.samilozturk.grad_samil.util.toUri
import com.samilozturk.grad_samil.util.toast
import com.samilozturk.grad_samil.validator.AddGalleryItemValidator
import kotlinx.coroutines.launch

class AddGalleryItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGalleryItemBinding
    private val validator = AddGalleryItemValidator()
    private val remoteStorageManager = RemoteStorageManager()
    private val dbManager = DbManager()
    private val authenticator = Authenticator()
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    private var mediaUri: Uri? = null
    private var thumbnailUri: Uri? = null
    private var isVideo = false
    private var title = ""

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val fileName = uri.getFileName(contentResolver)!!
                val extension = fileName.substringAfterLast(".")
                mediaUri = uri

                when (extension) {
                    in arrayOf("mp4", "mkv", "avi") -> {
                        binding.imageViewPlay.isVisible = true
                        binding.imageView.setOnClickListener {
                            openVideo(uri)
                        }
                        isVideo = true
                    }

                    in arrayOf("jpg", "jpeg", "png") -> {
                        binding.imageView.setImageURI(uri)
                        binding.imageViewPlay.isVisible = false
                        binding.imageView.setOnClickListener(null)
                        binding.imageView.setOnClickListener {
                            openImage(uri)
                        }
                        isVideo = false
                    }

                    else -> {
                        toast("Dosya formatı desteklenmiyor")
                        return@registerForActivityResult
                    }
                }

                Glide.with(this)
                    .load(uri)
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
                            val thumbnailBitmap = bitmap.getThumbnailBitmap()
                            thumbnailUri = thumbnailBitmap.toUri(contentResolver)
                            return false
                        }

                    })
                    .into(binding.imageView)


                binding.buttonPickImage.text = "Resmi/Videoyu Değiştir"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGalleryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // change home button icon
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        binding.buttonPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        binding.editTextTitle.doAfterTextChanged {
            title = it.toString()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_gallery_item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_save -> {
                // save announcement
                try {
                    val data = AddGalleryItemData(title, mediaUri, thumbnailUri)
                    validator.validate(data)
                    saveGalleryItem()
                } catch (e: Exception) {
                    snackbar(e.message.toString(), isError = true)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun saveGalleryItem() {
        lifecycleScope.launch {
            progressDialog.setMessage("Galeri öğesi ekleniyor...")
            progressDialog.show()
            try {
                val mediaPath =
                    if (isVideo)
                        remoteStorageManager.uploadVideo(
                            videoUri = mediaUri!!,
                            uriFileName = mediaUri!!.getFileName(contentResolver)!!
                        )
                    else
                        remoteStorageManager.uploadImage(
                            imageUri = mediaUri!!,
                            uriFileName = mediaUri!!.getFileName(contentResolver)!!
                        )
                val thumbnailPath = remoteStorageManager.uploadThumbnail(
                    thumbnailUri = thumbnailUri!!,
                    uriFileName = thumbnailUri!!.getFileName(contentResolver)!!
                )
                val galleryItem = GalleryItem(
                    title = title,
                    mediaUrl = mediaPath,
                    thumbnailUrl = thumbnailPath,
                    isVideo = isVideo
                )
                dbManager.createGalleryItem(galleryItem, authenticator.getCurrentUser()!!.uid)
                toast("Galeri öğesi başarıyla eklendi")
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun isAnyFieldFilled(): Boolean {
        return mediaUri != null || title.isNotEmpty()
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

}