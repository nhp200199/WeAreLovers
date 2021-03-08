package com.example.lovereminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
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

public class IniActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_yourName;
    private EditText edt_yourFrName;
    private TextView edt_date;
    private Button btn_confirm;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ini);
        showDialog();

        connectViews();

        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE);

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

                if (date.equals("Nhấn để chọn") || yourFrName.isEmpty() || yourName.isEmpty()) {
                    btn_confirm.setEnabled(false);
                } else btn_confirm.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        edt_date.addTextChangedListener(textWatcher);
        edt_yourFrName.addTextChangedListener(textWatcher);
        edt_yourName.addTextChangedListener(textWatcher);
    }

    private void showDialog() {
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
    }

    private void connectViews() {
        edt_yourName = findViewById(R.id.edt_yourName);
        edt_yourFrName = findViewById(R.id.edt_yourFrName);
        edt_date = findViewById(R.id.edt_date);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        edt_date.setOnClickListener(this);
    }

    private void collectInfor() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("yourName", edt_yourName.getText().toString());
        editor.putString("yourFrName", edt_yourFrName.getText().toString());
        editor.putString("date", edt_date.getText().toString());
        editor.apply();

        //setting the alarm
        setAlarm();
    }

    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);

        Intent intent = new Intent(this, CoupleDateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        //persist alarm when system restarts
        ComponentName receiver = new ComponentName(this, SystemBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                collectInfor();
                startActivity(new Intent(IniActivity.this, MainActivity.class));
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
                        //because the month is counted from 0
                        month = month + 1;
                        //TODO: reformat the text String.format
                        String date = dayOfMonth + "/" + month + "/" + year;
                        edt_date.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }
}
