package com.phucnguyen.lovereminder.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.feature.couple.coupleInstantiation.presentation.IniActivity
import com.phucnguyen.lovereminder.core.common.constant.PREF_COUPLE_DATE
import com.phucnguyen.lovereminder.core.base.presentation.BaseActivity
import com.phucnguyen.lovereminder.databinding.ActivitySplashBinding
import com.phucnguyen.lovereminder.di.PrefUserInfo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    @Inject
    @PrefUserInfo
    lateinit var sharedPreferences: SharedPreferences

    override fun getClassTag(): String {
        return this.javaClass.simpleName
    }

    override fun getViewBindingClass(inflater: LayoutInflater): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(inflater)
    }

    override fun setupView() {
    }

    override fun setViewListener() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        binding.tvWelcome.animation = animation
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        Handler(mainLooper).postDelayed({
            val intent = if (sharedPreferences.getLong(PREF_COUPLE_DATE, 0) == 0L) {
                Intent(applicationContext, IniActivity::class.java)
            } else {
                Intent(applicationContext, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, DELAY_SPLASH)
    }

    companion object {
        const val DELAY_SPLASH = 3000L
    }
}