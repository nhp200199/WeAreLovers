package com.phucnguyen.lovereminder.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.phucnguyen.lovereminder.ui.fragment.DiaryFragment;
import com.phucnguyen.lovereminder.ui.fragment.MainFragment;
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment;
import com.phucnguyen.lovereminder.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdRequest;

import java.io.FileNotFoundException;

public class MainActivity extends BaseActivity implements MainFragment.SettingsListener {
    private static final int TIME_TO_ACCEPT_CLOSE_APP = 5 * 1000;
    public static final int COUNT_DOWN_INTERVAL = 200;

    private int flag; //used to check exit

    private SharedPreferences sharedPreferences;
    private CountDownTimer timer;

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        Log.d("Tag", "Created");
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar.tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        retrieveUserInfor();

        setUpViewPager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isIgnoringBatteryOptimizations()) {
            openIgnoreBatteryOptimizationSettings();
        }

        setupAdView();
    }

    private void setupAdView() {
        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Start loading the ad in the background.
        mBinding.adView.loadAd(adRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        return isIgnoringBatteryOptimizations(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openIgnoreBatteryOptimizationSettings() {
        try {
            Toast.makeText(getApplicationContext(), "Battery optimization -> All apps -> mCare Lite -> Don't optimize", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swipeViewPager(int position) {
        mBinding.pager.setCurrentItem(position);
    }

    private void retrieveUserInfor() {
        sharedPreferences = getSharedPreferences("background", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE);

        if(sharedPreferences.contains("picture"))
        {
            Uri uri = Uri.parse(sharedPreferences.getString("picture", null));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(uri),
                        null,
                        options);

                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                Log.d("RESULT METRICS", "WIDTH: " + imageWidth);
                Log.d("RESULT METRICS", "HEIGHT: " + imageHeight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Glide.with(this)
                    .load(uri)
                    .into(mBinding.imgBackground);
        }
    }

    private void setUpViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mBinding.pager.setAdapter(sectionsPagerAdapter);
        mBinding.tlSwipe.setupWithViewPager(mBinding.pager);

        //disable click on tab layout
        for (View v : mBinding.tlSwipe.getTouchables()) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getIntent().hasExtra("position"))
            swipeViewPager(getIntent().getIntExtra("position", 0));

        mBinding.adView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.adView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.adView.destroy();
    }

    @Override
    public void onBackgroundImageChanged(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(mBinding.imgBackground);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("picture", uri.toString());
        editor.apply();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private static final int NUMBER_OF_PAGES = 3;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new DiaryFragment();
                case 2:
                    return new PictureFragment();
                default:
                    return new MainFragment();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (flag == 0) {
            configureTimerToExitApp();
            Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
        }
        flag++;
    }

    private void configureTimerToExitApp() {
        timer = new CountDownTimer(TIME_TO_ACCEPT_CLOSE_APP, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (flag == 2) {
                    this.cancel();
                    finish();
                }
            }

            @Override
            public void onFinish() {
                flag = 0;
            }
        };
        timer.start();
    }
}
