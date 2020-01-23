package com.example.lovereminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class IniActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edt_yourName;
    private EditText edt_yourFrName;
    private TextView edt_date;
    private Button btn_confirm;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ini);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hehe, khi em vào tới được đây rồi thì có nghĩa mình đã chính thức quen nhau được 1 năm rồi đó bé lùn tịt :))))")
                .setPositiveButton("I love you", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        edt_yourName = findViewById(R.id.edt_yourName);
        edt_yourFrName = findViewById(R.id.edt_yourFrName);
        edt_date = findViewById(R.id.edt_date);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        edt_date.setOnClickListener(this);

        sharedPreferences =  getSharedPreferences("userInfor", MODE_PRIVATE);

        btn_confirm.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String date = edt_date.getText().toString().trim();
                String yourName = edt_yourName.getText().toString().trim();
                String yourFrName = edt_yourFrName.getText().toString().trim();

                if(date.equals("Nhấn để chọn") || yourFrName.isEmpty() || yourName.isEmpty()){
                    btn_confirm.setEnabled(false);
                }
                else  btn_confirm.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        edt_date.addTextChangedListener(textWatcher);
        edt_yourFrName.addTextChangedListener(textWatcher);
        edt_yourName.addTextChangedListener(textWatcher);
        //disableBtnConfirm();
    }

    private void disableBtnConfirm() {
        while(edt_date.getText().toString().equals("") || edt_yourName.getText().toString().equals("") || edt_yourFrName.getText().toString().equals(""))
        {
            btn_confirm.setEnabled(false);
        }
        btn_confirm.setEnabled(true);
    }

    private void collectInfor() {
        if(edt_yourName.getText().toString().equals("") || edt_yourFrName.getText().toString().equals("") || edt_date.getText().toString().equals("Nhấn để chọn")){
            if(edt_yourName.getText().toString().equals(""))
                edt_yourName.setError("Chưa nhập");
            if(edt_yourFrName.getText().toString().equals(""))
                edt_yourName.setError("Chưa nhập");
            if(edt_date.getText().toString().equals("Nhấn để chọn"))
                edt_yourName.setError("Chưa nhập");
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("yourName", edt_yourName.getText().toString());
            editor.putString("yourFrName", edt_yourFrName.getText().toString());
            editor.putString("date", edt_date.getText().toString());
            editor.apply();

            Intent intent = new Intent (IniActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                collectInfor();
                finish();
                break;
            case R.id.edt_date:
                showDatePickerDialog();
                break;
        }
    }

    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth +"/" + month + "/" + year;
                        edt_date.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

}
