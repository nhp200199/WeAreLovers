package com.phucnguyen.lovereminder.di.activity

import com.phucnguyen.lovereminder.repository.DiaryRepo
import com.phucnguyen.lovereminder.repository.DiaryRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {
//    @Binds
//    abstract fun bindDiaryRepo(
//        diaryRepoImpl: DiaryRepoImpl
//    ): DiaryRepo
}