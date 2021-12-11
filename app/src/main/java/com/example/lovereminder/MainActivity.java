package com.example.lovereminder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lovereminder.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener, MainFragment.SettingsListener{
    public static final int REQ_CODE_CREATE_DIARY = 12;
    private static final int TIME_TO_ACCEPT_CLOSE_APP = 5 * 1000;

    private int flag; //used to check exit

    private String intentStr_YourName;
    private String intentStr_YourFrName;
    private String intentStr_Days;

    private ViewPager pager;
    private TextView tv_title;
    private ImageView img_background;
    private TabLayout tb_swipe;
    private Menu menu;
    private AdView adView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences1;
    private CustomCountDownTimer timer = new CustomCountDownTimer(TIME_TO_ACCEPT_CLOSE_APP, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("TIMER", "Counting down: " + millisUntilFinished / 1000);
            super.onTick(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            Log.d("TIMER", "Finished");
            super.onFinish();
        }
    };

    int height;
    int width;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        Log.d("Tag", "Created");
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mToolbar = binding.toolbar.tb;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        Log.d("METRICS", "WIDTH: " + width);
        Log.d("METRICS", "HEIGHT: " + height);

        connectViews(binding);

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
        adView.loadAd(adRequest);
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
        pager.setCurrentItem(position);
    }

    private void retrieveUserInfor() {
        sharedPreferences1 = getSharedPreferences("background", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE);

        if(sharedPreferences1.contains("picture"))
        {
            Uri uri = Uri.parse(sharedPreferences1.getString("picture", null));
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
                    .into(img_background);
        }

        intentStr_YourName = sharedPreferences.getString("yourName", null);
        intentStr_YourFrName = sharedPreferences.getString("yourFrName", null);
        intentStr_Days = sharedPreferences.getString("date", null);
    }

    private void setUpViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(sectionsPagerAdapter);
        tb_swipe.setupWithViewPager(pager);
        //tb_swipe.clearOnTabSelectedListeners();

        //disable click on tab layout
        for (View v : tb_swipe.getTouchables()) {
            v.setEnabled(false);
        }
    }

    private void connectViews(ActivityMainBinding binding) {
        img_background = binding.imgBackground;
        tv_title = binding.toolbar.tvTitle;
        tb_swipe = binding.tlSwipe;
        pager = binding.pager;
        adView = binding.adView;

        tv_title.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title:
//                showPopUpChangeBackGround();
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(getIntent().hasExtra("position"))
            swipeViewPager(getIntent().getIntExtra("position", 0));

        if (adView != null) {
            adView.resume();
        }
    }

    private void showPopUpChangeBackGround() {
        String[] arr = {"Đổi hình nền"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setActivityTitle("My Crop")
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setCropMenuCropButtonTitle("Done")
                                .setAspectRatio(width ,height - mToolbar.getHeight())
                                .setFixAspectRatio(true)
                                .setRequestedSize(width, height - mToolbar.getHeight(), CropImageView.RequestSizeOptions.RESIZE_EXACT)
                                //.setFixAspectRatio(true)
                                .start(MainActivity.this);
                        break;
                }

            }
        });
        builder.show();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            mToolbar.setVisibility(View.GONE);
            if (resultCode == RESULT_OK) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                try {
                    BitmapFactory.decodeStream(
                           getContentResolver().openInputStream(result.getUri()),
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
                        .load(result.getUri())
                        .into(img_background);


                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putString("picture", result.getUri().toString());
                editor.apply();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(MainActivity.this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public void onBackgroundImageChanged(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(img_background);


        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("picture", uri.toString());
        editor.apply();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    MainFragment mainFragment = new MainFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("yourName", intentStr_YourName);
                    bundle.putString("yourFrName", intentStr_YourFrName);
                    bundle.putString("date", intentStr_Days);
                    mainFragment.setArguments(bundle);
                    return mainFragment;
                case 1:
                    return new DiaryFragment();
                case 2:
                    return new PictureFragment();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (timer.isFinished) {
            timer.start();
        } else {
            if (flag == 1) {
                timer.cancel();
                super.onBackPressed();
            }
            else {
                flag = 1;
                Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
                timer.start();
            }
        }
    }

    private class CustomCountDownTimer extends CountDownTimer {
        private boolean isFinished = false;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isFinished = false;
        }

        @Override
        public void onFinish() {
            isFinished = true;
        }
    }
}
