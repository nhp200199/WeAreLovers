package com.phucnguyen.lovereminder.di.viewmodel

import com.phucnguyen.lovereminder.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    @Binds
    abstract fun bindDiaryRepo(
        diaryRepoImpl: DiaryRepoImpl
    ): DiaryRepo

    @Binds
    abstract fun bindUserRepo(
        userRepoImpl: UserRepoImpl
    ): UserRepo

    @Binds
    abstract fun bindPictureRepo(
        pictureRepoImpl: PictureRepoImpl
    ): PictureRepo
}