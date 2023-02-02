package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.phucnguyen.lovereminder.*

class IniActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var sharedPreferences: SharedPreferences
    private var userPreferences: SharedPreferences

    init {
        sharedPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_INFO,
            AppCompatActivity.MODE_PRIVATE
        )
        userPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_PREFERENCE,
            AppCompatActivity.MODE_PRIVATE
        )
        userPreferences.edit()
            .putInt(PREF_THEME_COLOR, R.color.colorPrimary)
            .apply()
    }

    fun saveUserInfo(yourName: String, yourFrName: String, coupleDate: String) {
        val editor = sharedPreferences.edit()
        editor.putString(PREF_YOUR_NAME, yourName)
        editor.putString(PREF_YOUR_FRIEND_NAME, yourFrName)
        editor.putString(PREF_COUPLE_DATE, coupleDate)
        editor.apply()
    }
}
