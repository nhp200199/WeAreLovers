package com.phucnguyen.lovereminder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.phucnguyen.lovereminder.model.Diary

@Database(version = 3, entities = [Diary::class, DiaryFTS::class])
abstract class AppDatabase : RoomDatabase() {
    abstract val diaryDao: DiaryDao

    companion object {
        private var instance: AppDatabase? = null
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    database.execSQL("alter table diaries rename to tmp")
                    database.execSQL("create table diaries (id integer primary key not null, date integer not null, content text not null)")
                    database.execSQL("drop table tmp")
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "couple_things.db")
                        .addMigrations(MIGRATION_1_2)
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return instance!!
        }
    }
}