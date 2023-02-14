package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.phucnguyen.lovereminder.PREF_BACKGROUND_PICTURE
import com.phucnguyen.lovereminder.SHARE_PREF_BACKGROUND
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.repository.DiaryRepoImpl

class CreateDiaryViewModel(application: Application,
        private val diaryRepo: DiaryRepoImpl) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(
        SHARE_PREF_BACKGROUND,
        AppCompatActivity.MODE_PRIVATE
    )

    fun getBackgroundImage(): String? {
        return sharedPreferences.getString(PREF_BACKGROUND_PICTURE, null)
    }

    suspend fun createDiary(diary: Diary): Long {
        return diaryRepo.createDiary(diary)
    }
}