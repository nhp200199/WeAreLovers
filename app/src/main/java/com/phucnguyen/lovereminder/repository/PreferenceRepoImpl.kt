package com.phucnguyen.lovereminder.repository

import android.content.SharedPreferences
import com.phucnguyen.lovereminder.PREF_BACKGROUND_PICTURE
import com.phucnguyen.lovereminder.PREF_THEME_ID
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.di.PrefPreferenceSetting
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PreferenceRepoImpl @Inject constructor(@PrefPreferenceSetting private val settingPreference: SharedPreferences) : PreferenceRepo {
    private val _backgroundPictureFlow = MutableSharedFlow<String?>(replay = 1)
    private val _appThemeFlow = MutableStateFlow(R.style.AppThemeBase_Rose)

    init {
        val currentBackgroundPicture = settingPreference.getString(PREF_BACKGROUND_PICTURE, null)
        _backgroundPictureFlow.tryEmit(currentBackgroundPicture)

        val currentAppTheme = settingPreference.getInt(PREF_THEME_ID, R.style.AppThemeBase_Rose)
        _appThemeFlow.tryEmit(currentAppTheme)
    }

    override fun getBackgroundPictureFlow(): Flow<String?> = _backgroundPictureFlow.asSharedFlow()

    override fun changeBackgroundPicture(newPicturePath: String) {
        _backgroundPictureFlow.tryEmit(newPicturePath)
        settingPreference.edit().putString(PREF_BACKGROUND_PICTURE, newPicturePath).apply()
    }

    override fun getAppThemeFlow(): Flow<Int> = _appThemeFlow.asSharedFlow().distinctUntilChanged()

    override fun changeAppTheme(newThemeId: Int) {
        _appThemeFlow.tryEmit(newThemeId)
        settingPreference.edit().putInt(PREF_THEME_ID, newThemeId).apply()
    }

    override fun getCurrentTheme(): Int = _appThemeFlow.value
}