package com.phucnguyen.lovereminder.feature.couple.data.datasource

import android.content.SharedPreferences
import com.phucnguyen.lovereminder.core.common.constant.PREF_COUPLE_DATE
import com.phucnguyen.lovereminder.core.common.constant.PREF_COUPLE_IMAGE
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_FRIEND_IMAGE
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_FRIEND_NAME
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_IMAGE
import com.phucnguyen.lovereminder.core.common.constant.PREF_YOUR_NAME
import com.phucnguyen.lovereminder.feature.couple.domain.datasource.ICoupleDataStore
import com.phucnguyen.lovereminder.di.PrefUserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CoupleDataStoreImpl @Inject constructor(
    @PrefUserInfo private val sharedPreferences: SharedPreferences
) : ICoupleDataStore {
    private val _yourNameFlow: MutableStateFlow<String?>
    private val _yourPartnerNameFlow: MutableStateFlow<String?>
    private val _coupleDateFlow: MutableStateFlow<Long>
    private val _coupleImageFlow: MutableStateFlow<String?>
    private val _yourImageFlow: MutableStateFlow<String?>
    private val _yourPartnerImageFlow: MutableStateFlow<String?>
    init {
        val yourName = sharedPreferences.getString(PREF_YOUR_NAME, null)
        val yourPartnerName = sharedPreferences.getString(PREF_YOUR_FRIEND_NAME, null)
        val coupeDateFlow = sharedPreferences.getLong(PREF_COUPLE_DATE, 0L)
        val yourImage = sharedPreferences.getString(PREF_YOUR_IMAGE, null)
        val yourPartnerImage = sharedPreferences.getString(PREF_YOUR_FRIEND_IMAGE, null)
        val coupleImage = sharedPreferences.getString(PREF_COUPLE_IMAGE, null)

        _yourNameFlow = MutableStateFlow(yourName)
        _yourPartnerNameFlow = MutableStateFlow(yourPartnerName)
        _coupleDateFlow = MutableStateFlow(coupeDateFlow)
        _yourImageFlow = MutableStateFlow(yourImage)
        _yourPartnerImageFlow = MutableStateFlow(yourPartnerImage)
        _coupleImageFlow = MutableStateFlow(coupleImage)
    }

    override fun getYourNameFlow(): Flow<String?> {
        return _yourNameFlow.asStateFlow()
    }

    override fun getYourPartnerNameFlow(): Flow<String?> {
        return _yourPartnerNameFlow.asStateFlow()
    }

    override fun getCoupleDateFlow(): Flow<Long> {
        return _coupleDateFlow.asStateFlow()
    }

    override fun getCoupleImageFlow(): Flow<String?> {
        return _coupleImageFlow.asStateFlow()
    }

    override fun getYourImageFlow(): Flow<String?> {
        return _yourImageFlow.asStateFlow()
    }

    override fun getYourPartnerImageFlow(): Flow<String?> {
        return _yourPartnerImageFlow.asStateFlow()
    }

    override fun setYourName(name: String) {
        _yourNameFlow.value = name
    }

    override fun setYourPartnerName(name: String) {
        _yourPartnerNameFlow.value = name
    }

    override fun setCoupleDate(date: Long) {
        _coupleDateFlow.value = date
    }

    override fun saveCoupleImage(image: String) {
        sharedPreferences.edit().putString(PREF_COUPLE_IMAGE, image).apply()
        _coupleImageFlow.value = image
    }

    override fun setYourImage(image: String) {
        _yourImageFlow.value = image
    }

    override fun setYourPartnerImage(image: String) {
        _yourPartnerImageFlow.value = image
    }

    override fun saveYourName() {
        sharedPreferences.edit().putString(PREF_YOUR_NAME, _yourNameFlow.value).apply()
    }

    override fun saveYourPartnerName() {
        sharedPreferences.edit().putString(PREF_YOUR_FRIEND_NAME, _yourPartnerNameFlow.value).apply()
    }

    override fun saveCoupleDate() {
        sharedPreferences.edit().putLong(PREF_COUPLE_DATE, _coupleDateFlow.value).apply()
    }

    override fun saveYourImage() {
        sharedPreferences.edit().putString(PREF_YOUR_IMAGE, _yourImageFlow.value).apply()
    }

    override fun saveYourPartnerImage() {
        sharedPreferences.edit().putString(PREF_YOUR_FRIEND_IMAGE, _yourPartnerImageFlow.value).apply()
    }
}