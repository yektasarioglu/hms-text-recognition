package com.yektasarioglu.textrecognition.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

fun Uri.toDrawable(context: Context) : Drawable {
    val inputStream = context.contentResolver?.openInputStream(this)
    return Drawable.createFromStream(inputStream, this.toString())
}