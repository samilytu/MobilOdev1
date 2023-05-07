package com.samilozturk.grad_samil.data

import android.net.Uri

data class AddGalleryItemData(
    val title: String = "",
    val mediaUri: Uri? = null,
    val thumbnailUri: Uri? = null,
)