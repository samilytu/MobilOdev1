package com.samilozturk.grad_samil.util

import android.app.Activity
import com.google.android.material.snackbar.Snackbar
import com.samilozturk.grad_samil.R

fun Activity.snackbar(message: String, isError: Boolean = false) {
    val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
    if (isError) {
        snackbar.setBackgroundTint(resources.getColor(R.color.error))
    }
    snackbar.show()
}