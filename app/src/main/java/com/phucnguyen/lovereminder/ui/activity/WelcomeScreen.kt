package com.phucnguyen.lovereminder.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.phucnguyen.lovereminder.R
import android.widget.TextView
import android.content.Intent
import android.view.animation.AnimationUtils
import com.phucnguyen.lovereminder.PREF_YOUR_NAME
import com.phucnguyen.lovereminder.SHARE_PREF_USER_INFO
import com.phucnguyen.lovereminder.databinding.ActivityWelcomeScreenBinding
import com.phucnguyen.lovereminder.ui.activity.IniActivity
import com.phucnguyen.lovereminder.ui.activity.MainActivity

class WelcomeScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(SHARE_PREF_USER_INFO, MODE_PRIVATE)
        val tvWelcome = binding.tvWelcome
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        tvWelcome.animation = animation
        val timer: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                    if (sharedPreferences.getString(PREF_YOUR_NAME, "")!!.isEmpty()) {
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