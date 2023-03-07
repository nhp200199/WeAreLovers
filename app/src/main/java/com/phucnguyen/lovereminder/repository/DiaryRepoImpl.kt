package com.phucnguyen.lovereminder.repository

import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.model.Diary
import kotlinx.coroutines.flow.Flow

class DiaryRepoImpl(private val diaryDao: DiaryDao) : DiaryRepo {
    override fun findAllDiaries(): Flow<List<Diary>> {
        return diaryDao.findAllDiaries()
    }

    override suspend fun findById(id: Int): Flow<Diary> {
        return diaryDao.findById(id)
    }

    override suspend fun updateDiary(diary: Diary): Int {
        return diaryDao.updateDiary(diary)
    }

    override suspend fun deleteDiary(diary: Diary): Int {
        return diaryDao.deleteDiary(diary)
    }

    override suspend fun createDiary(diary: Diary): Long {
        return diaryDao.insertDiary(diary)
    }
}