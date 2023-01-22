package com.phucnguyen.lovereminder.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.phucnguyen.lovereminder.R
import android.widget.TextView
import android.content.Intent
import android.view.animation.AnimationUtils
import com.phucnguyen.lovereminder.ui.activity.IniActivity
import com.phucnguyen.lovereminder.ui.activity.MainActivity

class WelcomeScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE)
        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        tvWelcome.animation = animation
        val timer: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                    if (sharedPreferences.getString("yourName", "") === "") {
                        val intent = Intent(applicationContext, IniActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                    super.run()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        timer.start()
    }
}