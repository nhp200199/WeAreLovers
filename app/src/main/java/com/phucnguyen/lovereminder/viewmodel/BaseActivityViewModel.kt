package com.phucnguyen.lovereminder.viewmodel

import androidx.lifecycle.ViewModel
import com.phucnguyen.lovereminder.repository.PreferenceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BaseActivityViewModel @Inject constructor(private val repo: PreferenceRepo): ViewModel() {
    val themeFlow = repo.getAppThemeFlow()
    private var previousTheme = repo.getCurrentTheme()
    fun isThemeChanged() = previousTheme != repo.getCurrentTheme()
    fun getCurrentTheme(): Int = repo.getCurrentTheme()
    fun updatePreviousTheme() {
        previousTheme = repo.getCurrentTheme()
    }
}