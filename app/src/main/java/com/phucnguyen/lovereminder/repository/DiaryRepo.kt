package com.phucnguyen.lovereminder.repository

import com.phucnguyen.lovereminder.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepo {
    fun findAllDiaries(): Flow<List<Diary>>
//    suspend fun findById(id: Int): Diary
//    suspend fun updateDiary(diary: Diary): Int
    suspend fun deleteDiary(diary: Diary): Int
    suspend fun createDiary(diary: Diary): Long
}