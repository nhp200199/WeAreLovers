package com.phucnguyen.lovereminder.di.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.phucnguyen.lovereminder.SHARE_PREF_BACKGROUND
import com.phucnguyen.lovereminder.SHARE_PREF_USER_INFO
import com.phucnguyen.lovereminder.SHARE_PREF_USER_PREFERENCE
import com.phucnguyen.lovereminder.database.AppDatabase
import com.phucnguyen.lovereminder.database.DiaryDao
import com.phucnguyen.lovereminder.di.PrefBackgroundPicture
import com.phucnguyen.lovereminder.di.PrefPreferenceSetting
import com.phucnguyen.lovereminder.di.PrefUserInfo
import com.phucnguyen.lovereminder.repository.PreferenceRepo
import com.phucnguyen.lovereminder.repository.PreferenceRepoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    companion object {
        @Provides
        fun db(application: Application): AppDatabase = AppDatabase.getInstance(application)

        @Provides
        fun diaryDao(db: AppDatabase): DiaryDao = db.diaryDao

        @Provides
        @PrefBackgroundPicture
        fun prefBackgroundPicture(application: Application): SharedPreferences = application.getSharedPreferences(
            SHARE_PREF_BACKGROUND, AppCompatActivity.MODE_PRIVATE)

        @Provides
        @PrefUserInfo
        fun prefUserInfo(application: Application): SharedPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_INFO, AppCompatActivity.MODE_PRIVATE)

        @Provides
        @PrefPreferenceSetting
        fun prefPreferenceSetting(application: Application): SharedPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_PREFERENCE, AppCompatActivity.MODE_PRIVATE)

        @Singleton
        @Provides
        fun provideIODispatcher() = Dispatchers.IO
    }

    @Binds
    @Singleton
    abstract fun bindPreferenceSettingRepo(
        preferenceRepoImpl: PreferenceRepoImpl
    ): PreferenceRepo
}