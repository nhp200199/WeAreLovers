package com.phucnguyen.lovereminder.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.di.PrefUserInfo
import com.phucnguyen.lovereminder.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IniActivityViewModel @Inject constructor(
    private val userRepo: UserRepo,
) : ViewModel() {
    fun saveUserInfo(yourName: String, yourFrName: String, coupleDate: String) {
        userRepo.run {
            setYourName(yourName)
            setYourFrName(yourFrName)
            setCoupleDate(coupleDate)
        }
    }
}
