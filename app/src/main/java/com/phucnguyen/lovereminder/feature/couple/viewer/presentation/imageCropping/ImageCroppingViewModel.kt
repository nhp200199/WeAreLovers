package com.phucnguyen.lovereminder.feature.couple.viewer.presentation.imageCropping

import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import com.phucnguyen.lovereminder.feature.couple.common.presentation.enums.ChangeTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageCroppingViewModel @Inject constructor(
    private val coupleRepository: ICoupleRepository
) : ViewModel() {
    fun onImageCropped(target: ChangeTarget, imageUri: String) {
        when(target) {
            ChangeTarget.YOU -> {
                coupleRepository.setYourImage(imageUri)
            }
            ChangeTarget.YOUR_PARTNER -> {
                coupleRepository.setYourPartnerImage(imageUri)
            }
            ChangeTarget.BACKGROUND -> {
                coupleRepository.saveCoupleImage(imageUri)
            }
            else -> throw NotImplementedError()
        }
    }
}