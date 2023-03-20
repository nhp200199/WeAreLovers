package com.phucnguyen.lovereminder.di.app

import android.app.Application
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
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
}