package com.example.lovereminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.GoalRow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DiaryActivity extends AppCompatActivity{
    private TextView tv_Date;
    private TextView tv_Content;
    private EditText edt_Content;
    private Menu menu;
    private ImageButton imb_back;
    private ArrayList<Diary> diaries;

    private String txtBeforeChanged="";
    private String txtAfterChanged="";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        tv_Content = findViewById(R.id.tv_content);
        tv_Date = findViewById(R.id.tv_date);
        imb_back = findViewById(R.id.img_back);
        edt_Content = findViewById(R.id.edt_content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = getSharedPreferences("lst_diary", MODE_PRIVATE);

        tv_Date.setText(getIntent().getStringExtra("date"));
        tv_Content.setText(getIntent().getStringExtra("content"));

        edt_Content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text1 = edt_Content.getText().toString();
                String text2 = tv_Content.getText().toString();
                if(text1.equals(text2)){
                    menu.clear();
                }
                else {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.done, menu);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imb_back.setVisibility(View.VISIBLE);

    }

    public void backtoMainActivity(View view){
        txtBeforeChanged = tv_Content.getText().toString();
        txtAfterChanged = edt_Content.getText().toString();

        if(txtAfterChanged.equals(txtBeforeChanged)){
            Intent intent = new Intent(DiaryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            showMessage();
        }




    }

    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Chưa lưu thay dổi. Bạn có muốn lưu lại không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DiaryActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                txtBeforeChanged = tv_Content.getText().toString();
                txtAfterChanged = tv_Content.getText().toString();

                edt_Content.setVisibility(View.VISIBLE);
                edt_Content.setText(tv_Content.getText().toString());
                edt_Content.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_Content, InputMethodManager.SHOW_IMPLICIT);
                edt_Content.setSelection(edt_Content.getText().length());
                tv_Content.setVisibility(View.INVISIBLE);

                menu.clear();


                return true;
            case R.id.action_done_rewrite_diary:
                tv_Content.setText(edt_Content.getText().toString().trim());
                tv_Content.setVisibility(View.VISIBLE);
                edt_Content.setVisibility(View.INVISIBLE);

                saveDiary();

                menu.clear();
                getMenuInflater().inflate(R.menu.menu_main, menu);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDiary() {
        txtBeforeChanged = tv_Content.getText().toString();
        txtAfterChanged = tv_Content.getText().toString();

        position = getIntent().getIntExtra("lst_position", 0);

        gson = new Gson();
        String json1 = sharedPreferences.getString("lst_diary", null);
        Type type =  new TypeToken<ArrayList<Diary>>(){}.getType();
        diaries = gson.fromJson(json1, type);
        diaries.get(position).setContent(edt_Content.getText().toString().trim());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(diaries);
        editor.putString("lst_diary", json);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        backtoMainActivity(imb_back);
    }
}
