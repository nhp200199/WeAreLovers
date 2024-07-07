package com.phucnguyen.lovereminder.feature.couple.domain.datasource

import kotlinx.coroutines.flow.Flow

interface ICoupleDataStore {
    fun getYourNameFlow(): Flow<String?>
    fun getYourPartnerNameFlow(): Flow<String?>
    fun getCoupleDateFlow(): Flow<Long>
    fun getCoupleImageFlow(): Flow<String?>
    fun getYourImageFlow(): Flow<String?>
    fun getYourPartnerImageFlow(): Flow<String?>
    fun setYourName(name: String)
    fun setYourPartnerName(name: String)
    fun setCoupleDate(date: Long)
    fun saveCoupleImage(image: String)
    fun setYourImage(image: String)
    fun setYourPartnerImage(image: String)
    fun saveYourName()
    fun saveYourPartnerName()
    fun saveCoupleDate()
    fun saveYourImage()
    fun saveYourPartnerImage()
}