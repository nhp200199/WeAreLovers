package com.phucnguyen.lovereminder.model

import android.net.Uri

data class Image(
    val id: Long,
    val uri: Uri,
    val description: String
)