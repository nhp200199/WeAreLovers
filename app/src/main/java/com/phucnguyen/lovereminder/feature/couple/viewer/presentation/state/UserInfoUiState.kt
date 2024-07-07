package com.phucnguyen.lovereminder.feature.couple.viewer.presentation.state

import android.net.Uri

data class UserInfoUiState(
    val yourName: String,
    val yourFrName: String,
    val yourImage: Uri,
    val yourFrImage: Uri,
    val coupleDate: String
)

val DEFAULT_USER_INFO_UI_STATE = UserInfoUiState(
    "",
    "",
    Uri.EMPTY,
    Uri.EMPTY,
    ""
)