package com.phucnguyen.lovereminder.feature.couple.coupleInstantiation.presentation.state

data class CoupleInstantiationFormUIState(
    val yourNameErrors: List<String> = emptyList(),
    val yourPartnerNameErrors: List<String> = emptyList(),
    val coupleDateErrors: List<String> = emptyList(),
    val isFormValid:Boolean = false
) {
}