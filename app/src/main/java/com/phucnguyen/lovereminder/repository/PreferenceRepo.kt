package com.phucnguyen.lovereminder.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepo {
    fun getBackgroundPictureFlow(): Flow<String?>
    fun changeBackgroundPicture(newPicturePath: String)
    fun getAppThemeFlow(): Flow<Int>
    fun changeAppTheme(newThemeId: Int)
    fun getCurrentTheme(): Int
}