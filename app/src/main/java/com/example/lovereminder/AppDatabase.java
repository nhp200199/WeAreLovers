package com.example.lovereminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(version = 1, entities = {Diary.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract DiaryDao getDiaryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "couple_things.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
