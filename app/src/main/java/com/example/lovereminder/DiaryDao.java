package com.example.lovereminder;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface DiaryDao {
    @Query("select * from diaries order by date desc")
//    LiveData<List<Diary>> getAllDiaries();
    Observable<List<Diary>> getAllDiaries();

    @Insert
    Completable insertDiary(Diary diary);

    @Delete
    Completable deleteDiary(Diary diary);

    @Update
    Completable updateDiary(Diary diary);

    @Query("select * from diaries where id = :id")
//    LiveData<Diary> getDiaryById(int id);
//    Observable<Diary> getDiaryById (int id);
    Single<Diary> getDiaryById(int id);
}
