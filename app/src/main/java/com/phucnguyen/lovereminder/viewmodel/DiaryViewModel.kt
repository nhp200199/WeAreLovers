package com.phucnguyen.lovereminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.database.AppDatabase.Companion.getInstance
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.repository.DiaryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(private val diaryRepo: DiaryRepo) : ViewModel() {
    private val _isEditingFlow = MutableStateFlow(false)
    val isEditingFlow = _isEditingFlow.asStateFlow()
    var originalText: String? = null
    private var currentDiary: Diary? = null

    fun setDiaryEditingState(isEditing: Boolean) {
        _isEditingFlow.tryEmit(isEditing)
    }

    fun isEditingDiary(): Boolean {
        return isEditingFlow.value
    }

    suspend fun updateDiary(newContent: String): Boolean {
        val updateDiary = currentDiary!!.copy(content = newContent)
        val updateResult = diaryRepo.updateDiary(updateDiary)
        return updateResult > 0
    }

    suspend fun findDiaryById(id: Int): Flow<Diary> {
        return diaryRepo.findById(id).onEach {
            currentDiary = it
            originalText = currentDiary!!.content
        }
    }

    fun isDiaryContentChanged(newContent: String): Boolean = newContent != originalText
}