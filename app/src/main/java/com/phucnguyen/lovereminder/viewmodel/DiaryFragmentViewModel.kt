package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import kotlinx.coroutines.flow.Flow

class DiaryFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val mDiaryDao: DiaryDao = AppDatabase.getInstance(application).diaryDao

    fun getAllDiaries(): Flow<List<Diary>> {
        return mDiaryDao.findAllDiaries()
    }

    suspend fun deleteDiary(diary: Diary): Int {
        return mDiaryDao.deleteDiary(diary)
    }
}