package com.phucnguyen.lovereminder.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.phucnguyen.lovereminder.databinding.ActivityMainBinding
import com.phucnguyen.lovereminder.ui.fragment.DiaryFragment
import com.phucnguyen.lovereminder.ui.fragment.MainFragment
import com.phucnguyen.lovereminder.ui.fragment.MainFragment.SettingsListener
import com.phucnguyen.lovereminder.ui.fragment.PictureFragment
import java.io.FileNotFoundException

class MainActivity : BaseActivity(), SettingsListener {
    private var flag //used to check exit
            = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var timer: CountDownTimer
    private var mBinding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        Log.d("Tag", "Created")
        mBinding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(mBinding!!.root)
        setSupportActionBar(mBinding!!.toolbar.tb)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        retrieveUserInfor()
        setUpViewPager()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isIgnoringBatteryOptimizations) {
            openIgnoreBatteryOptimizationSettings()
        }
        setupAdView()
    }

    private fun setupAdView() {
        // Create an ad request.
        val adRequest = AdRequest.Builder()
            .build()

        // Start loading the ad in the background.
        mBinding!!.adView.loadAd(adRequest)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
    }

    @get:RequiresApi(api = Build.VERSION_CODES.M)
    private val isIgnoringBatteryOptimizations: Boolean
        private get() = isIgnoringBatteryOptimizations(this)

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun openIgnoreBatteryOptimizationSettings() {
        try {
            Toast.makeText(
                applicationContext,
                "Battery optimization -> All apps -> mCare Lite -> Don't optimize",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent()
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun swipeViewPager(position: Int) {
        mBinding!!.pager.currentItem = position
    }

    private fun retrieveUserInfor() {
        sharedPreferences = getSharedPreferences("background", MODE_PRIVATE)
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE)
        if (sharedPreferences.contains("picture")) {
            val uri = Uri.parse(sharedPreferences.getString("picture", null))
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            try {
                BitmapFactory.decodeStream(
                    contentResolver.openInputStream(uri),
                    null,
                    options
                )
                val imageHeight = options.outHeight
                val imageWidth = options.outWidth
                Log.d("RESULT METRICS", "WIDTH: $imageWidth")
                Log.d("RESULT METRICS", "HEIGHT: $imageHeight")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            Glide.with(this)
                .load(uri)
                .into(mBinding!!.imgBackground)
        }
    }

    private fun setUpViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mBinding!!.pager.adapter = sectionsPagerAdapter
        mBinding!!.tlSwipe.setupWithViewPager(mBinding!!.pager)

        //disable click on tab layout
        for (v in mBinding!!.tlSwipe.touchables) {
            v.isEnabled = false
        }
    }

    public override fun onResume() {
        super.onResume()
        if (intent.hasExtra("position")) swipeViewPager(intent.getIntExtra("position", 0))
        mBinding!!.adView.resume()
    }

    override fun onPause() {
        super.onPause()
        mBinding!!.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.adView.destroy()
    }

    override fun onBackgroundImageChanged(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(mBinding!!.imgBackground)
        val editor = sharedPreferences!!.edit()
        editor.putString("picture", uri.toString())
        editor.apply()
    }

    private class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        override fun getCount(): Int {
            return Companion.NUMBER_OF_PAGES
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                1 -> DiaryFragment()
                2 -> PictureFragment()
                else -> MainFragment()
            }
        }

        companion object {
            private const val NUMBER_OF_PAGES = 3
        }
    }

    override fun onBackPressed() {
        if (flag == 0) {
            configureTimerToExitApp()
            Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show()
        }
        flag++
    }

    private fun configureTimerToExitApp() {
        timer = object :
            CountDownTimer(TIME_TO_ACCEPT_CLOSE_APP.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                if (flag == 2) {
                    cancel()
                    finish()
                }
            }

            override fun onFinish() {
                flag = 0
            }
        }
        timer.start()
    }

    companion object {
        private const val TIME_TO_ACCEPT_CLOSE_APP = 5 * 1000
        const val COUNT_DOWN_INTERVAL = 200
    }
}