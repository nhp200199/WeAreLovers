package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.repository.UserRepo
import kotlinx.coroutines.flow.Flow

class IniActivityViewModel(application: Application, private val userRepo: UserRepo) : AndroidViewModel(application) {
    private var userPreferences: SharedPreferences

    //TODO: move SharedPreference to Data layer
    init {
        userPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_PREFERENCE,
            AppCompatActivity.MODE_PRIVATE
        )
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
