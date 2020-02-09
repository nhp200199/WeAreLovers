package com.example.lovereminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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

        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    if(sharedPreferences.getString("yourName", "") == "")
                    {
                        Intent intent = new Intent(getApplicationContext(), IniActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        };
        timer.start();
    }


}
