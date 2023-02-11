package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.repository.DiaryRepoImpl
import java.lang.IllegalArgumentException

class ViewModelFactory(val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CreateDiaryViewModel::class.java)) {
            return CreateDiaryViewModel(application, DiaryRepoImpl(AppDatabase.getInstance(application).diaryDao)) as T
        } else {
            throw IllegalArgumentException("Cannot find the view model")
        }
    }
}