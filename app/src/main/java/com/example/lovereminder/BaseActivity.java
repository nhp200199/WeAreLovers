package com.example.lovereminder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private static final String PREF_USER_SETTINGS = "user_prefs";
    public static final String FIELD_THEME = "theme_id";
    private int mCurrentThemeId = R.style.AppTheme;
    private SharedPreferences mSharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mSharedPref = getSharedPreferences(PREF_USER_SETTINGS, MODE_PRIVATE);
        mCurrentThemeId = mSharedPref.getInt(FIELD_THEME, mCurrentThemeId);
    }

    protected void setTheme() {setTheme(mCurrentThemeId);}

    protected void switchTheme(int themeId) {
        mCurrentThemeId = themeId;
        mSharedPref.edit().putInt(FIELD_THEME, mCurrentThemeId).apply();
        recreate();
    }
}
