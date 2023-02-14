package com.phucnguyen.lovereminder.ui.activity

import androidx.appcompat.app.AppCompatActivity
import com.phucnguyen.lovereminder.R
import android.content.SharedPreferences
import android.os.Bundle
import com.phucnguyen.lovereminder.ui.activity.BaseActivity

open class BaseActivity : AppCompatActivity() {
    private var mCurrentThemeId = R.style.AppThemeBase_Rose
    private lateinit var mSharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPref = getSharedPreferences(PREF_USER_SETTINGS, MODE_PRIVATE)
        mCurrentThemeId = mSharedPref.getInt(FIELD_THEME, mCurrentThemeId)
    }

    protected fun setTheme() {
        setTheme(mCurrentThemeId)
    }

    fun switchTheme(themeId: Int) {
        mCurrentThemeId = themeId
        mSharedPref.edit().putInt(FIELD_THEME, mCurrentThemeId).apply()
        recreate()
    }

    companion object {
        private const val PREF_USER_SETTINGS = "user_prefs"
        const val FIELD_THEME = "theme_id"
    }
}