package com.phucnguyen.lovereminder.repository

import android.content.SharedPreferences
import com.phucnguyen.lovereminder.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UserRepoImpl(private val userSharePref: SharedPreferences) : UserRepo {
    private val _yourNameFlow = MutableSharedFlow<String>(replay = 1)
    private val _yourFrNameFlow = MutableSharedFlow<String>(replay = 1)
    private val _coupleDateFlow = MutableSharedFlow<String>(replay = 1)

    init {
        _yourNameFlow.tryEmit(userSharePref.getString(PREF_YOUR_NAME, "")!!)
        _yourFrNameFlow.tryEmit(userSharePref.getString(PREF_YOUR_FRIEND_NAME, "")!!)
        _coupleDateFlow.tryEmit(userSharePref.getString(PREF_COUPLE_DATE, "26/12/1965")!!)
    }

    override fun getYourNameFlow(): Flow<String> {
        return _yourNameFlow.asSharedFlow()
    }

    override fun getYourFrNameFlow(): Flow<String> {
        return _yourFrNameFlow.asSharedFlow()
    }

    override fun getYourImage(): String {
        return userSharePref.getString(PREF_YOUR_IMAGE, "")!!
    }

    override fun getYourFrImage(): String {
        return userSharePref.getString(PREF_YOUR_FRIEND_IMAGE, "")!!
    }

    override fun getCoupleDateFlow(): Flow<String> {
        return _coupleDateFlow.asSharedFlow()
    }

    override fun setYourName(name: String) {
        _yourNameFlow.tryEmit(name)
        userSharePref.edit().putString(PREF_YOUR_NAME, name).apply()
    }

    override fun setYourFrName(name: String) {
        _yourFrNameFlow.tryEmit(name)
        userSharePref.edit().putString(PREF_YOUR_FRIEND_NAME, name).apply()
    }

    override fun setYourImage(uriPath: String) {
        userSharePref.edit().putString(PREF_YOUR_IMAGE, uriPath).apply()
    }

    override fun setYourFrImage(uriPath: String) {
        userSharePref.edit().putString(PREF_YOUR_FRIEND_IMAGE, uriPath).apply()
    }

    override fun setCoupleDate(date: String) {
        userSharePref.edit().putString(PREF_COUPLE_DATE, date).apply()
    }
}