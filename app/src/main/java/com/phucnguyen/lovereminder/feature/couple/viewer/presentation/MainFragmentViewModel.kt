package com.phucnguyen.lovereminder.feature.couple.viewer.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.state.DEFAULT_USER_INFO_UI_STATE
import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.state.UserInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class ChangeTarget {
    YOU,
    YOUR_PARTNER,
    BACKGROUND
}

@HiltViewModel
class MainFragmentViewModel @Inject constructor(private val coupleRepository: ICoupleRepository) : ViewModel() {
    private val _userInfoUiStateFlow: StateFlow<UserInfoUiState> = combine(coupleRepository.getYourNameFlow(),
        coupleRepository.getYourImageFlow(),
        coupleRepository.getYourPartnerNameFlow(),
        coupleRepository.getYourPartnerImageFlow(),
        coupleRepository.getCoupleDateFlow()
    ) {
            yourName, yourImage, yourFrName, yourFrImage, coupleDate ->
        val yourImageUri = Uri.parse(yourImage)
        val yourFrImageUri = Uri.parse(yourFrImage)
        val coupleDayCounts = getCoupleDaysCount(coupleDate)
        UserInfoUiState(
            yourName ?: "",
            yourFrName ?: "",
            yourImageUri,
            yourFrImageUri,
            coupleDayCounts.toString())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        DEFAULT_USER_INFO_UI_STATE
    )
    val userInfoUiStateFlow: Flow<UserInfoUiState> = _userInfoUiStateFlow

    private val _isEditingCoupleData = MutableStateFlow<Boolean>(false)
    val isEditingCoupleDataFlow: Flow<Boolean> = _isEditingCoupleData.drop(1)

    private var changeTarget: ChangeTarget? = null

    init {
        viewModelScope.launch {
            _isEditingCoupleData.collect { isEditing ->
                if(!isEditing) {
                    changeTarget = null
                }
            }
        }
    }

    private fun getCoupleDaysCount(coupleDate: Long): Long {
        val current = Calendar.getInstance().timeInMillis
        val diffTimestamps = current - coupleDate
        return TimeUnit.DAYS.convert(diffTimestamps, TimeUnit.MILLISECONDS)
    }

    fun setCoupleDate(date: Long) {
        coupleRepository.setCoupleDate(date)
    }

    fun cancelEditCoupleData() {
        setIsEditingCoupleData(false)
    }

    fun saveCoupleData() {
        coupleRepository.saveYourName()
        coupleRepository.saveYourPartnerName()
        coupleRepository.saveCoupleDate()
        coupleRepository.saveYourImage()
        coupleRepository.saveYourPartnerImage()
    }

    fun setIsEditingCoupleData(isEditing: Boolean) {
        _isEditingCoupleData.value = isEditing
    }

    fun isEditingCoupleData(): Boolean {
        return _isEditingCoupleData.value
    }

    fun onCoupleAvatarSelected(path: String) {
        when(changeTarget) {
            ChangeTarget.YOU -> coupleRepository.setYourImage(path)
            ChangeTarget.YOUR_PARTNER -> coupleRepository.setYourPartnerImage(path)
            else -> return
        }
    }

    fun onChangeImage(path: String) {
        when(changeTarget) {
            ChangeTarget.BACKGROUND -> coupleRepository.saveCoupleImage(path)
            ChangeTarget.YOU -> coupleRepository.setYourImage(path)
            ChangeTarget.YOUR_PARTNER -> coupleRepository.setYourPartnerImage(path)
            else -> return
        }
    }

    fun targetChanged(target: ChangeTarget) {
        changeTarget = target
    }

    fun getTarget(): ChangeTarget? {
        return changeTarget
    }
}