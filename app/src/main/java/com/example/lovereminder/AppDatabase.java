package com.example.lovereminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(version = 2, entities = {Diary.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract DiaryDao getDiaryDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.beginTransaction();
            try {
                database.execSQL("alter table diaries rename to tmp");
                database.execSQL("create table diaries (id integer primary key not null, date integer not null, content text not null)");
                database.execSQL("drop table tmp");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "couple_things.db")
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

