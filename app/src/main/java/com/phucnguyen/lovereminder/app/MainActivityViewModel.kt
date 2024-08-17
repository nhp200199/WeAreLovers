package com.phucnguyen.lovereminder.app

import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val coupleRepository: ICoupleRepository
) : ViewModel() {
    val backgroundImageFlow = coupleRepository.getCoupleImageFlow()
}