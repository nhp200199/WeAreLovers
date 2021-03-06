package com.example.lovereminder;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DiaryDao {
    @Query("select * from diaries order by date desc")
    LiveData<List<Diary>> getAllDiaries();

    @Insert
    Void insertDiary(Diary diary);

    @Delete
    void deleteDiary(Diary diary);

    @Update
    int updateDiary(Diary diary);

    @Query("select * from diaries where id = :id")
    LiveData<Diary> getDiaryById(int id);
}
