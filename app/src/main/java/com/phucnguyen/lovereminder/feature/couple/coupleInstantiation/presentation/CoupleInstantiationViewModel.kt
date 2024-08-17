package com.phucnguyen.lovereminder.feature.couple.coupleInstantiation.presentation

import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.feature.couple.common.domain.repository.ICoupleRepository
import com.phucnguyen.lovereminder.core.utils.formatDate
import com.phucnguyen.lovereminder.core.utils.parseDateTimestamps
import com.phucnguyen.lovereminder.feature.couple.coupleInstantiation.presentation.state.CoupleInstantiationFormUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CoupleInstantiationViewModel @Inject constructor(
    private val coupleRepository: ICoupleRepository
) : ViewModel() {

    private val _yourNameInputStateFlow = coupleRepository.getYourNameFlow()

    private val _yourPartnerNameInputStateFlow = coupleRepository.getYourPartnerNameFlow()
    private val _coupleDateStateFlow = coupleRepository.getCoupleDateFlow()
    val coupleDateStateFlow = _coupleDateStateFlow.filter { it != 0L }
        .map { formatDate(it) }
    private val _yourNameInputErrorStateFlow = _yourNameInputStateFlow.map {
        val errors = mutableListOf<String>()
        if (it == null) {
            errors.add("Init")
        } else {
            if (it.isBlank()) {
                errors.add("Your name is required")
            }
        }
        errors
    }
    private val _yourPartnerNameInputErrorStateFlow = _yourPartnerNameInputStateFlow.map {
        val errors = mutableListOf<String>()
        if (it == null) {
            errors.add("Init")
        } else {
            if (it.isBlank()) {
                errors.add("Your partner's name is required")
            }
        }
        errors
    }

    private val _coupleDateErrorStateFlow = _coupleDateStateFlow.map {
        val errors = mutableListOf<String>()
        if (it == 0L) {
            errors.add("Init")
        }
        errors
    }

    val coupleInstantiationUIState = combine(_yourNameInputErrorStateFlow,
            _yourPartnerNameInputErrorStateFlow, _coupleDateErrorStateFlow) { yourNameErrors, yourPartnerErrors, coupleDateErrors ->
        return@combine CoupleInstantiationFormUIState(
            yourNameErrors,
            yourPartnerErrors,
            coupleDateErrors,
            isFormValid = yourNameErrors.isEmpty() && yourPartnerErrors.isEmpty() && coupleDateErrors.isEmpty()
        )
    }

    fun setCoupleDate(date: String) {
        coupleRepository.setCoupleDate(parseDateTimestamps(date))
    }

    fun setYourNameInput(input: String) {
        coupleRepository.setYourName(input)
    }

    fun setYourPartnerNameInput(input: String) {
        coupleRepository.setYourPartnerName(input)
    }

    fun saveYourName() {
        coupleRepository.saveYourName()
    }

    fun saveYourPartnerName() {
        coupleRepository.saveYourPartnerName()
    }

    fun saveCoupleDate() {
        coupleRepository.saveCoupleDate()
    }
}