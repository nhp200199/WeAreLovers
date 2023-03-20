package com.phucnguyen.lovereminder.di.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.phucnguyen.lovereminder.SHARE_PREF_BACKGROUND
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.di.PrefBackgroundPicture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun db(application: Application): AppDatabase = AppDatabase.getInstance(application)

    @Provides
    fun diaryDao(db: AppDatabase): DiaryDao = db.diaryDao

    @Provides
    @PrefBackgroundPicture
    fun prefBackgroundPicture(application: Application): SharedPreferences = application.getSharedPreferences(
        SHARE_PREF_BACKGROUND, AppCompatActivity.MODE_PRIVATE)
}