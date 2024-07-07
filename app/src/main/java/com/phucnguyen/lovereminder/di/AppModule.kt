package com.phucnguyen.lovereminder.di

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.phucnguyen.lovereminder.feature.couple.data.datasource.CoupleDataStoreImpl
import com.phucnguyen.lovereminder.feature.couple.domain.datasource.ICoupleDataStore
import com.phucnguyen.lovereminder.core.common.constant.SHARE_PREF_USER_INFO
import com.phucnguyen.lovereminder.core.common.constant.SHARE_PREF_USER_PREFERENCE
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Singleton
    @Binds
    abstract fun coupleDataStore(coupleDataStoreImpl: CoupleDataStoreImpl): ICoupleDataStore

    companion object {
        @Singleton
        @Provides
        @PrefUserInfo
        fun prefUserInfo(application: Application): SharedPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_INFO, AppCompatActivity.MODE_PRIVATE)

        @Singleton
        @Provides
        @PrefPreferenceSetting
        fun prePreferencesSetting(application: Application): SharedPreferences = application.getSharedPreferences(
            SHARE_PREF_USER_PREFERENCE, AppCompatActivity.MODE_PRIVATE)
    }
}