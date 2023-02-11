package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val mDiaryDao: DiaryDao
    private val _isEditingFlow = MutableStateFlow(false)
    val isEditingFlow = _isEditingFlow.asStateFlow()
    var originalText: String? = null
    private var currentDiary: Diary? = null

    init {
        mDiaryDao = getInstance(application).diaryDao
    }

    fun setDiaryEditingState(isEditing: Boolean) {
        _isEditingFlow.tryEmit(isEditing)
    }

    fun isEditingDiary(): Boolean {
        return isEditingFlow.value
    }

    suspend fun updateDiary(newContent: String): Boolean {
        val updateDiary = currentDiary!!.copy(content = newContent)
        val updateResult = mDiaryDao.updateDiary(updateDiary)
        return updateResult > 0
    }

    fun findDiaryById(id: Int): Flow<Diary> {
        return mDiaryDao.findById(id).onEach {
            currentDiary = it
            originalText = currentDiary!!.content
        }
    }

    fun isDiaryContentChanged(newContent: String): Boolean = newContent != originalText
}