package com.phucnguyen.lovereminder.di

import com.phucnguyen.lovereminder.feature.couple.common.data.repository.CoupleRepositoryImpl
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    @Binds
    abstract fun coupleRepository(coupleRepositoryImpl: CoupleRepositoryImpl): ICoupleRepository
}