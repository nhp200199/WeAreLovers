package com.phucnguyen.lovereminder.repository

import kotlinx.coroutines.flow.Flow

interface UserRepo {
    fun getYourNameFlow(): Flow<String>
    fun getYourFrNameFlow(): Flow<String>
    fun getYourImageFlow(): Flow<String?>
    fun getYourFrImageFlow(): Flow<String?>
    fun getCoupleDateFlow(): Flow<String>
    fun setYourName(name: String)
    fun setYourFrName(name: String)
    fun setYourImage(uriPath: String)
    fun setYourFrImage(uriPath: String)
    fun setCoupleDate(date: String)
}