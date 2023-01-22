package com.phucnguyen.lovereminder.database

import androidx.room.*
import com.phucnguyen.lovereminder.model.Diary
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface DiaryDao {
    @get:Query("select * from diaries order by date desc")
    val allDiaries: Observable<List<Diary>>

    @Insert
    fun insertDiary(diary: Diary): Completable

    @Delete
    fun deleteDiary(diary: Diary?): Completable

    @Update
    fun updateDiary(diary: Diary): Completable

    @Query("select * from diaries where id = :id")
    fun getDiaryById(id: Int): Single<Diary>

    @Query("select * from diaries join diaries_fts on diaries_fts.content = diaries.content where diaries_fts match :query")
    fun getDiariesBasedOnString(query: String): Single<List<Diary>>
}