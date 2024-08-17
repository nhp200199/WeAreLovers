package com.phucnguyen.lovereminder.di

import com.phucnguyen.lovereminder.core.common.permission.IPermissionHelper
import com.phucnguyen.lovereminder.core.common.permission.PermissionHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun permissionHelper(permissionHelperImpl: PermissionHelperImpl): IPermissionHelper

}