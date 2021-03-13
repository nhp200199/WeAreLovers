package com.example.lovereminder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateDiaryActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnSave;
    private EditText edt_diary;
    private ImageView img_background;

    private boolean isSaved = false;
    private SharedPreferences sharedPreferences1;
    private int page_number;
    int flag = 0;

    private DiaryDao mDiaryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        btnSave = findViewById(R.id.btn_save);
        edt_diary = findViewById(R.id.edt_diary);
        img_background = findViewById(R.id.img_background);

        page_number = getIntent().getIntExtra("page_number", 0);
        sharedPreferences1 = getSharedPreferences("background", MODE_PRIVATE);

        if(sharedPreferences1.contains("picture"))
        {
            Glide.with(this)
                    .load(Uri.parse(sharedPreferences1.getString("picture", null)))
                    .into(img_background);
        }

        btnSave.setEnabled(false);
        btnSave.setOnClickListener(this);

        edt_diary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = edt_diary.getText().toString().trim();
                btnSave.setEnabled(!string.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDiaryDao = AppDatabase.getInstance(this).getDiaryDao();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                saveDiary();
                break;
        }
    }

    private void saveDiary() {
        flag =1;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String content = edt_diary.getText().toString();
        String date = String.format("%d-%d-%d", year, month, day);

        Diary diary = new Diary();
        diary.setDate(date);
        diary.setContent(content);

        new InsertDiaryAsync(mDiaryDao).execute(diary);

        page_number = getIntent().getIntExtra("page_number", page_number);

        edt_diary.setText("");
        hideKeyboard(this);
    }

    @Override
    public void onBackPressed() {
        if(isSaved == false){
            if(edt_diary.getText().toString().equals("")){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("position", page_number);
                startActivity(intent);
                CreateDiaryActivity.super.onBackPressed();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Chưa lưu đoạn nhật kí kìa ấy ơi. Bạn có muốn lưu lại không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("position", page_number);
                                startActivity(intent);
                                CreateDiaryActivity.super.onBackPressed();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }


        }
        else if(isSaved)
        {
            finish();
            CreateDiaryActivity.super.onBackPressed();
        }

        
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private static class InsertDiaryAsync extends AsyncTask<Diary, Void, Void>{
        private DiaryDao mDiaryDao;

        public InsertDiaryAsync(DiaryDao diaryDao) {
            mDiaryDao = diaryDao;
        }

        @Override
        protected Void doInBackground(Diary... diaries) {
            return mDiaryDao.insestDiary(diaries[0]);
        }
    }
}
