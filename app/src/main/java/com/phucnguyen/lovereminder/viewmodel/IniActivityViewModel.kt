package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.di.PrefUserInfo
import com.phucnguyen.lovereminder.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class IniActivityViewModel @Inject constructor(
    private val userRepo: UserRepo,
    @PrefUserInfo private val userPreferences: SharedPreferences
) : ViewModel() {
    init {
        userPreferences.edit()
            .putInt(PREF_THEME_COLOR, R.color.amaranth)
            .apply()
    }

    fun saveUserInfo(yourName: String, yourFrName: String, coupleDate: String) {
        userRepo.run {
            setYourName(yourName)
            setYourFrName(yourFrName)
            setCoupleDate(coupleDate)
        }
    }
}
