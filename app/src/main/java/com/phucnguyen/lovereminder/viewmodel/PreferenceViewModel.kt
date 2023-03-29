package com.phucnguyen.lovereminder.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.repository.PreferenceRepo
import com.phucnguyen.lovereminder.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val preferenceRepo: PreferenceRepo
): ViewModel() {
    private val defaultImageUri = Uri.parse("android.resource://com.phucnguyen.lovereminder/drawable/couple")

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
}
