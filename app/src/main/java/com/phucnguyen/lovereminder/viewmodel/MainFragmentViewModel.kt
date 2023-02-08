package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.repository.UserRepo
import com.phucnguyen.lovereminder.ui.uiState.UserInfoUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MainFragmentViewModel(application: Application, private val userRepo: UserRepo) : AndroidViewModel(application) {
    val userInfoUiStateFlow: Flow<UserInfoUiState>

    init {
        userInfoUiStateFlow = combine(userRepo.getYourNameFlow(), userRepo.getYourFrNameFlow(), userRepo.getCoupleDateFlow()) {
                yourName, yourFrName, coupleDate ->
            UserInfoUiState(yourName, yourFrName, coupleDate)
        }
    }

    fun updateYourName(newName: String) {
        userRepo.setYourName(newName)
    }

    fun updateYourFrName(newName: String) {
        userRepo.setYourFrName(newName)
    }
}