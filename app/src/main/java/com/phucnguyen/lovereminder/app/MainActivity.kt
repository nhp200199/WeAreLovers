package com.phucnguyen.lovereminder.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.app.mainPager.PagerFragment
import com.phucnguyen.lovereminder.core.base.presentation.BaseActivity
import com.phucnguyen.lovereminder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var closeAppFlag = 0 // used to check exit
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d("Tag", "Created")
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar.tb)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
////        retrieveUserInfor()
//        setUpViewPager()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isIgnoringBatteryOptimizations(this)) {
//            openIgnoreBatteryOptimizationSettings()
//        }

        if (savedInstanceState == null) {
            val fragment = PagerFragment()

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    override fun getClassTag(): String {
        return MainActivity::class.java.simpleName
    }

    override fun getViewBindingClass(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun setupView() {
    }

    override fun setViewListener() {
//        onBackPressedDispatcher.addCallback(this, onBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (closeAppFlag == 0) {
//                    configureTimerToExitApp()
//                    Toast.makeText(this@MainActivity, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show()
//                }
//                closeAppFlag++
//            }
//
//        })
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName) ?: false
    }

    private fun openIgnoreBatteryOptimizationSettings() {
        try {
            Toast.makeText(
                applicationContext,
                "Battery optimization -> All apps -> WeAreLovers -> Don't optimize",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent()
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    private fun retrieveUserInfor() {
//        sharedPreferences = getSharedPreferences(SHARE_PREF_BACKGROUND, MODE_PRIVATE)
//        sharedPreferences = getSharedPreferences(SHARE_PREF_USER_INFO, MODE_PRIVATE)
//        if (sharedPreferences.contains(PREF_BACKGROUND_PICTURE)) {
//            val uri = Uri.parse(sharedPreferences.getString(PREF_BACKGROUND_PICTURE, null))
//            val options = BitmapFactory.Options()
//            options.inJustDecodeBounds = true
//            try {
//                BitmapFactory.decodeStream(
//                    contentResolver.openInputStream(uri),
//                    null,
//                    options
//                )
//                val imageHeight = options.outHeight
//                val imageWidth = options.outWidth
//                Log.d("RESULT METRICS", "WIDTH: $imageWidth")
//                Log.d("RESULT METRICS", "HEIGHT: $imageHeight")
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//            Glide.with(this)
//                .load(uri)
//                .into(binding.imgBackground)
//        }
//    }

    public override fun onResume() {
        super.onResume()
//        if (intent.hasExtra("position")) swipeViewPager(intent.getIntExtra("position", 0))
    }

//    private fun configureTimerToExitApp() {
//        timer = object :
//            CountDownTimer(TIME_TO_ACCEPT_CLOSE_APP.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
//            override fun onTick(millisUntilFinished: Long) {
//                if (closeAppFlag == 2) {
//                    cancel()
//                    finish()
//                }
//            }
//
//            override fun onFinish() {
//                closeAppFlag = 0
//            }
//        }
//        timer.start()
//    }

    companion object {
        private const val TIME_TO_ACCEPT_CLOSE_APP = 5 * 1000
        const val COUNT_DOWN_INTERVAL = 200
    }
}