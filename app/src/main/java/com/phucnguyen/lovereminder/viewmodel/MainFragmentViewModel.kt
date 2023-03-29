package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.repository.UserRepo
import com.phucnguyen.lovereminder.ui.uiState.UserInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {
    private val defaultImageUri = Uri.parse("android.resource://com.phucnguyen.lovereminder/drawable/couple")
    val userInfoUiStateFlow: Flow<UserInfoUiState>
    var flag = 0 // to distinguish you from your friend

    init {
        userInfoUiStateFlow = combine(userRepo.getYourNameFlow(),
            userRepo.getYourFrNameFlow(),
            userRepo.getYourImageFlow(),
            userRepo.getYourFrImageFlow(),
            userRepo.getCoupleDateFlow()
        ) {
                yourName, yourFrName, yourImage, yourFrImage, coupleDate ->
            val yourImageUri = if (yourImage == null) defaultImageUri else Uri.parse(yourImage)
            val yourFrImageUri = if (yourFrImage == null) defaultImageUri else Uri.parse(yourFrImage)
            UserInfoUiState(yourName, yourFrName, yourImageUri, yourFrImageUri, coupleDate)
        }
    }

    fun updateYourName(newName: String) {
        userRepo.setYourName(newName)
    }

    fun updateYourFrName(newName: String) {
        userRepo.setYourFrName(newName)
    }

    fun updateCoupleDate(date: String) {
        userRepo.setCoupleDate(date)
    }

    fun updateYourImage(newImage: String) {
        userRepo.setYourImage(newImage)
    }

    fun updateYourFrImage(newImage: String) {
        userRepo.setYourFrImage(newImage)
    }
}