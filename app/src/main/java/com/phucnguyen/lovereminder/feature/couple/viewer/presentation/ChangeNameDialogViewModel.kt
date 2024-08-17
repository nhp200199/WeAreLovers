package com.phucnguyen.lovereminder.feature.couple.viewer.presentation

import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_FRIEND_NAME
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeNameDialogViewModel @Inject constructor(
    private val coupleDataRepository: ICoupleRepository
): ViewModel() {
    fun onNameChanged(target: String?, name: String) {
        if (target != null) {
            if (target == PREF_YOUR_NAME) {
                coupleDataRepository.setYourName(name)
            } else if (target == PREF_YOUR_FRIEND_NAME) {
                coupleDataRepository.setYourPartnerName(name)
            }
        }
    }
}