package com.example.lovereminder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQ_CODE_CREATE_DIARY = 12;

    private int flag =0; //used to check exit

    private String intentStr_YourName;
    private String intentStr_YourFrName;
    private String intentStr_Days;
    private TextView tv_title;
    private ImageView img_background;
    private TabLayout tb_swipe;
    private Menu menu;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences1;

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Tag", "Created");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        img_background = (ImageView) findViewById(R.id.img_background);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tb_swipe = (TabLayout) findViewById(R.id.tl_swipe);

        sharedPreferences1 = getSharedPreferences("background", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE);

        if(sharedPreferences1.contains("picture"))
        {
            Glide.with(this)
                    .load(Uri.parse(sharedPreferences1.getString("picture", null)))
                    .into(img_background);
        }

        intentStr_YourName = sharedPreferences.getString("yourName", "");
        intentStr_YourFrName = sharedPreferences.getString("yourFrName", "");
        intentStr_Days = sharedPreferences.getString("date", "");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(sectionsPagerAdapter);
        tb_swipe.setupWithViewPager(pager);
        tb_swipe.clearOnTabSelectedListeners();

        //disable click on tab layout
        for (View v : tb_swipe.getTouchables()) {
            v.setEnabled(false);
        }

        tv_title.setOnClickListener(this);
        img_background.setOnClickListener(this);
        pager.setOnClickListener(this);

        if(getIntent().hasExtra("position"))
            pager.setCurrentItem(getIntent().getIntExtra("position", 0));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_dairy:
        //Code to run when the Create Order item is clicked
                Intent intent = new Intent(this, CreateDiaryActivity.class);
                int PageNumber = pager.getCurrentItem();
                intent.putExtra("page_number", PageNumber);
                flag = 0;
                startActivity(intent);
                finish();
                return true;
            case R.id.action_delete_picture:
                return  false;
            case R.id.action_cancel:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title:
                showPopUpChangeBackGround();
                break;
            case R.id.img_background:
                showPopUpChangeBackGround();

                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

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
                                .setRequestedSize(400, 400)
                                .setMaxCropResultSize(1800, 5000)
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
            if (resultCode == RESULT_OK) {
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
        flag++;
        if(flag != 2)
            Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
        else super.onBackPressed();

    }
}
