package com.phucnguyen.lovereminder.repository

import android.content.SharedPreferences
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.di.PrefUserInfo
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepoImpl @Inject constructor(@PrefUserInfo private val userSharePref: SharedPreferences) : UserRepo {
    private val _yourNameFlow = MutableSharedFlow<String>(replay = 1)
    private val _yourFrNameFlow = MutableSharedFlow<String>(replay = 1)
    private val _yourImageFlow = MutableSharedFlow<String>(replay = 1)
    private val _yourFrImageFlow = MutableSharedFlow<String>(replay = 1)
    private val _coupleDateFlow = MutableSharedFlow<String>(replay = 1)

    init {
        _yourNameFlow.tryEmit(userSharePref.getString(PREF_YOUR_NAME, "")!!)
        _yourFrNameFlow.tryEmit(userSharePref.getString(PREF_YOUR_FRIEND_NAME, "")!!)
        _coupleDateFlow.tryEmit(userSharePref.getString(PREF_COUPLE_DATE, "26/12/1965")!!)
        _yourImageFlow.tryEmit(userSharePref.getString(PREF_YOUR_IMAGE, "")!!)
        _yourFrImageFlow.tryEmit(userSharePref.getString(PREF_YOUR_FRIEND_IMAGE, "")!!)
    }

    override fun getYourNameFlow(): Flow<String> {
        return _yourNameFlow.asSharedFlow()
    }

    override fun getYourFrNameFlow(): Flow<String> {
        return _yourFrNameFlow.asSharedFlow()
    }

    override fun getYourImageFlow(): Flow<String> {
        return _yourImageFlow.asSharedFlow()
    }

    override fun getYourFrImageFlow(): Flow<String> {
        return _yourFrImageFlow.asSharedFlow()
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
        _yourImageFlow.tryEmit(uriPath)
        userSharePref.edit().putString(PREF_YOUR_IMAGE, uriPath).apply()
    }

    override fun setYourFrImage(uriPath: String) {
        _yourFrImageFlow.tryEmit(uriPath)
        userSharePref.edit().putString(PREF_YOUR_FRIEND_IMAGE, uriPath).apply()
    }

    override fun setCoupleDate(date: String) {
        _coupleDateFlow.tryEmit(date)
        userSharePref.edit().putString(PREF_COUPLE_DATE, date).apply()
    }
}