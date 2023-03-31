package com.phucnguyen.lovereminder.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.model.ColorTheme
import com.phucnguyen.lovereminder.repository.PreferenceRepo
import com.phucnguyen.lovereminder.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val preferenceRepo: PreferenceRepo
): ViewModel() {
    private val defaultImageUri = Uri.parse("android.resource://com.phucnguyen.lovereminder/drawable/couple")

    private val defaultColorThemes = flowOf(
        listOf(
            ColorTheme(R.color.amaranth, false),
            ColorTheme(R.color.royal_blue, false),
        )
    )
    private val _selectedColorTheme = MutableStateFlow<Int>(R.color.amaranth)
    val previewColorThemesFlow = _selectedColorTheme.flatMapLatest { id ->
        defaultColorThemes.map { theme ->
            theme.forEach { item ->
                item.isSelected = item.colorThemeResId == id
            }
            theme
        }
    }

    val selectedThemeFlow = _selectedColorTheme.map {
        when(it) {
            R.color.amaranth -> R.style.AppThemeBase_Rose
            R.color.royal_blue -> R.style.AppThemeBase_Blue
            else -> throw IllegalStateException()
        }
    }

    private val _isEditingYourName = MutableStateFlow<Boolean>(false)
    val isEditingYourNameFlow = _isEditingYourName.asStateFlow()
    val isEditingYourName: Boolean get() = _isEditingYourName.value

    private val _isEditingYourFriendName = MutableStateFlow<Boolean>(false)
    val isEditingYourFriendNameFlow = _isEditingYourFriendName.asStateFlow()
    val isEditingYourFriendName: Boolean get() = _isEditingYourFriendName.value

    val userNameFlow = userRepo.getYourNameFlow()
    val userFriendNameFlow = userRepo.getYourFrNameFlow()

    val backgroundPictureFlow = preferenceRepo.getBackgroundPictureFlow()
        .map { picturePath ->  if (picturePath != null) Uri.parse(picturePath) else defaultImageUri}
    val yourAvatarFlow = userRepo.getYourImageFlow()
        .map { picturePath ->  if (picturePath != null) Uri.parse(picturePath) else defaultImageUri }
    val yourFriendAvatarFlow = userRepo.getYourFrImageFlow()
        .map { picturePath ->  if (picturePath != null) Uri.parse(picturePath) else defaultImageUri }

    val coupleDateFlow = userRepo.getCoupleDateFlow()

    var changeTarget: Int = UNDEFINE_CHANGE_TARGET

    init {
        val currentColorThemeId = when (preferenceRepo.getCurrentTheme()) {
            R.style.AppThemeBase_Rose -> R.color.amaranth
            R.style.AppThemeBase_Blue -> R.color.royal_blue
            else -> throw java.lang.IllegalStateException("Cannot identify current theme value")
        }
        _selectedColorTheme.tryEmit(currentColorThemeId)
    }

    fun changeColorTheme(newColorThemeResId: Int) {
        _selectedColorTheme.tryEmit(newColorThemeResId)
    }

    fun changeAppTheme(newThemeResId: Int) {
        preferenceRepo.changeAppTheme(newThemeResId)
    }

    fun startEditYourName() {
        _isEditingYourName.tryEmit(true)
    }

    fun stopEditYourName() {
        _isEditingYourName.tryEmit(false)
    }

    fun startEditYourFriendName() {
        _isEditingYourFriendName.tryEmit(true)
    }

    fun stopEditYourFriendName() {
        _isEditingYourFriendName.tryEmit(false)
    }

    fun changeYourName(newName: String) {
        userRepo.setYourName(newName)
    }

    fun changeYourFriendName(newName: String) {
        userRepo.setYourFrName(newName)
    }

    fun changeYourAvatar(newPath: String) {
        userRepo.setYourImage(newPath)
    }

    fun changeYourFriendAvatar(newPath: String) {
        userRepo.setYourFrImage(newPath)
    }

    fun changeBackgroundPicture(newPath: String) {
        preferenceRepo.changeBackgroundPicture(newPath)
    }

    fun updateCoupleDate(newCoupleDate: String) {
        userRepo.setCoupleDate(newCoupleDate)
    }

    companion object {
        const val UNDEFINE_CHANGE_TARGET: Int = 0
        const val CHANGE_TARGET_YOU: Int = 1
        const val CHANGE_TARGET_YOUR_FRIEND: Int = 2
        const val CHANGE_TARGET_BACKGROUND_PICTURE: Int = 3
    }
}
