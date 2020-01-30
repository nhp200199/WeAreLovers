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
    private ArrayList<Diary> lst_diary;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences1;
    private int page_number;
    int flag = 0;

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
        sharedPreferences = getSharedPreferences("lst_diary", MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences("background", MODE_PRIVATE);

        if(sharedPreferences1.contains("picture"))
        {
            Glide.with(this)
                    .load(Uri.parse(sharedPreferences1.getString("picture", null)))
                    .into(img_background);
        }

        btnSave = findViewById(R.id.btn_save);
        edt_diary = findViewById(R.id.edt_diary);

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
        loadListDiary();

    }


    private void loadListDiary() {
        if(sharedPreferences.getString("lst_diary", null) != null){
            Gson gson = new Gson();
            String json = sharedPreferences.getString("lst_diary", null);
            Type type =  new TypeToken<ArrayList<Diary>>(){}.getType();
            lst_diary = gson.fromJson(json, type);
        }
        else{
            lst_diary=  new ArrayList<Diary>();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                flag =1;
                Calendar calendar = Calendar.getInstance();
                String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                String month = String.valueOf(calendar.get(Calendar.MONTH) +1);
                String year = String.valueOf(calendar.get(Calendar.YEAR));
                final String content = edt_diary.getText().toString();
                final String date = "Ngày "+ day + " Tháng " + month + " Năm " + year;

                page_number = getIntent().getIntExtra("page_number", page_number);

                lst_diary.add(new Diary(date, content));
                saveListDiary();

                Snackbar snackbar = Snackbar.make(findViewById(R.id.relative), "Đã lưu nhật kí", Snackbar.LENGTH_LONG);
                snackbar.setAction("Xem", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                        intent.putExtra("date", date);
                        intent.putExtra("content",content);
                        startActivity(intent);
                        finish();
                    }
                });
                snackbar.show();
                edt_diary.setText("");
                hideKeyboard(this);
                break;


        }
    }

    private void saveListDiary() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lst_diary);
        editor.putString("lst_diary", json);
        editor.apply();
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
}
