package com.phucnguyen.lovereminder.database

import androidx.room.*
import com.phucnguyen.lovereminder.model.Diary
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @get:Query("select * from diaries order by date desc")
    val allDiaries: Observable<List<Diary>>

    @Insert
    fun insertDiary(diary: Diary): Completable

    @Delete
    fun deleteDiary(diary: Diary?): Completable

    @Update
    suspend fun updateDiary(diary: Diary): Int

    @Query("select * from diaries where id = :id")
    fun findById(id: Int): Flow<Diary>

    @Query("select * from diaries join diaries_fts on diaries_fts.content = diaries.content where diaries_fts match :query")
    fun getDiariesBasedOnString(query: String): Single<List<Diary>>
}