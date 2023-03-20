package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.repository.DiaryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DiaryFragmentViewModel @Inject constructor(private val diaryRepo: DiaryRepo) : ViewModel() {

    fun getAllDiaries(): Flow<List<Diary>> {
        return diaryRepo.findAllDiaries()
    }

    suspend fun deleteDiary(diary: Diary): Int {
        return diaryRepo.deleteDiary(diary)
    }
}