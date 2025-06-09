package com.phucnguyen.lovereminder.app.mainContainer

import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val coupleRepository: ICoupleRepository
) : ViewModel() {
    val backgroundImageFlow = coupleRepository.getCoupleImageFlow()
}