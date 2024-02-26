package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phucnguyen.lovereminder.SHARE_PREF_USER_INFO
import java.lang.IllegalArgumentException
import com.phucnguyen.lovereminder.repository.UserRepoImpl

class ViewModelFactory(val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
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
            throw IllegalArgumentException("Cannot find the view model")
        }
    }
}