package com.phucnguyen.lovereminder.ui.uiState

import android.net.Uri

data class UserInfoUiState(
    val yourName: String,
    val yourFrName: String,
    val yourImage: Uri,
    val yourFrImage: Uri,
    val coupleDate: String
)
