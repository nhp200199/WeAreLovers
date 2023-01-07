package com.phucnguyen.lovereminder.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.phucnguyen.lovereminder.R;

public class BaseActivity extends AppCompatActivity {
    private static final String PREF_USER_SETTINGS = "user_prefs";
    public static final String FIELD_THEME = "theme_id";
    private int mCurrentThemeId = R.style.AppThemeBase_Rose;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = getSharedPreferences(PREF_USER_SETTINGS, MODE_PRIVATE);
        mCurrentThemeId = mSharedPref.getInt(FIELD_THEME, mCurrentThemeId);
    }

    protected void setTheme() {
        setTheme(mCurrentThemeId);
    }

    public void switchTheme(int themeId) {
        mCurrentThemeId = themeId;
        mSharedPref.edit().putInt(FIELD_THEME, mCurrentThemeId).apply();
        recreate();
    }
}
