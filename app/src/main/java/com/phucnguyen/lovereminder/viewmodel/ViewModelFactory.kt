package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.phucnguyen.lovereminder.SHARE_PREF_USER_INFO
import com.phucnguyen.lovereminder.repository.UserRepoImpl

class ViewModelFactory(private val application: Application) : AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(IniActivityViewModel::class.java)) {
            return IniActivityViewModel(application,
                UserRepoImpl(application.getSharedPreferences(SHARE_PREF_USER_INFO, AppCompatActivity.MODE_PRIVATE))
            ) as T
        } else if (modelClass.isAssignableFrom(MainFragmentViewModel::class.java)) {
            return MainFragmentViewModel(application,
                UserRepoImpl(application.getSharedPreferences(SHARE_PREF_USER_INFO, AppCompatActivity.MODE_PRIVATE))
            ) as T
        } else {
            return super.create(modelClass)
        }
    }
}