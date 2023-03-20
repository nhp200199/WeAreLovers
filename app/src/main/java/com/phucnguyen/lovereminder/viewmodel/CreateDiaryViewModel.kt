package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phucnguyen.lovereminder.PREF_BACKGROUND_PICTURE
import com.phucnguyen.lovereminder.SHARE_PREF_BACKGROUND
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.di.PrefBackgroundPicture
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.repository.DiaryRepo
import com.phucnguyen.lovereminder.repository.DiaryRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateDiaryViewModel @Inject constructor(
    private val diaryRepo: DiaryRepo,
    @PrefBackgroundPicture private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun getBackgroundImage(): String? {
        return sharedPreferences.getString(PREF_BACKGROUND_PICTURE, null)
    }

    suspend fun createDiary(diary: Diary): Long {
        return diaryRepo.createDiary(diary)
    }
}