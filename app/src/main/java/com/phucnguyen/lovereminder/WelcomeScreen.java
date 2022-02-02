package com.phucnguyen.lovereminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class WelcomeScreen extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome_screen);
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE);

        TextView tvWelcome = findViewById(R.id.tv_welcome);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        tvWelcome.setAnimation(animation);

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    if (sharedPreferences.getString("yourName", "") == "") {
                        Intent intent = new Intent(getApplicationContext(), IniActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}
