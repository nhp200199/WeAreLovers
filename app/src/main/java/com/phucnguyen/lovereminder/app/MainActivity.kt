package com.phucnguyen.lovereminder.app

import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.MainFragment
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.core.base.presentation.BaseActivity
import com.phucnguyen.lovereminder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var closeAppFlag = 0 // used to check exit
    private lateinit var timer: CountDownTimer
    override fun getClassTag(): String {
        return MainActivity::class.java.simpleName
    }

    override fun getViewBindingClass(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun setupView() {
        setSupportActionBar(binding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setUpViewPager()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backgroundImageFlow.collect {
                    Glide.with(this@MainActivity)
                        .load(it)
                        .into(binding.imgBackground)
                }
            }
        }
    }

    override fun setViewListener() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (closeAppFlag == 0) {
                    configureTimerToExitApp()
                    Toast.makeText(this@MainActivity, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show()
                }
                closeAppFlag++
            }

        })
    }
//    private lateinit var binding: ActivityMainBinding

//    private lateinit var adView: AdView
//    private var initialLayoutComplete = false
//    // Determine the screen width (less decorations) to use for the ad width.
//    // If the ad hasn't been laid out, default to the full screen width.
//    private val myAdSize: AdSize
//        get() {
//            val display = windowManager.defaultDisplay
//            val outMetrics = DisplayMetrics()
//            display.getMetrics(outMetrics)
//
//            val density = outMetrics.density
//
//            var adWidthPixels = binding.adViewContainer.width.toFloat()
//            if (adWidthPixels == 0f) {
//                adWidthPixels = outMetrics.widthPixels.toFloat()
//            }
//
//            val adWidth = (adWidthPixels / density).toInt()
//            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
//        }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
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
////        setupAdView()
//    }

//    private fun setupAdView() {
//        // Initialize the Mobile Ads SDK with an AdMob App ID.
//        MobileAds.initialize(this) {}
//
//        // Set your test devices. Check your logcat output for the hashed device ID to
//        // get test ads on a physical device. e.g.
//        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
//        // to get test ads on this device."
//        MobileAds.setRequestConfiguration(
//            RequestConfiguration.Builder().setTestDeviceIds(listOf("AC2FDCECBCC0ADA1D187ED08618252FD")).build()
//        )
//
//        adView = AdView(this)
//        adView.adListener = object : AdListener() {
//            override fun onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            override fun onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                // Code to be executed when an ad request fails.
//            }
//
//            override fun onAdImpression() {
//                // Code to be executed when an impression is recorded
//                // for an ad.
//            }
//
//            override fun onAdLoaded() {
//                binding.adViewContainer.visibility = View.VISIBLE
//            }
//
//            override fun onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//        }
//
//        binding.adViewContainer.addView(adView)
//        // Since we're loading the banner based on the adContainerView size, we need to wait until this
//        // view is laid out before we can get the width.
//        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
//            if (!initialLayoutComplete) {
//                initialLayoutComplete = true
//                loadBanner()
//            }
//        }
//    }
//
//    private fun loadBanner() {
//        adView.adUnitId = getString(R.string.banner_ad_unit_id)
//
//        adView.adSize = myAdSize
//
//        // Create an ad request.
//        val adRequest = AdRequest.Builder().build()
//
//        // Start loading the ad in the background.
//        adView.loadAd(adRequest)
//    }

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

    private fun swipeViewPager(position: Int) {
        binding.pager.currentItem = position
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

    private fun setUpViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        binding.pager.adapter = sectionsPagerAdapter
        binding.tlSwipe.setupWithViewPager(binding.pager)

        //disable click on tab layout
        for (v in binding.tlSwipe.touchables) {
            v.isEnabled = false
        }
    }

    public override fun onResume() {
        super.onResume()
//        if (intent.hasExtra("position")) swipeViewPager(intent.getIntExtra("position", 0))
//        adView.resume()
    }

    override fun onPause() {
        super.onPause()
//        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
//        adView.destroy()
    }

    private class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        override fun getCount(): Int {
            return NUMBER_OF_PAGES
        }

        override fun getItem(position: Int): Fragment {
            return MainFragment()
        }

        companion object {
            private const val NUMBER_OF_PAGES = 1
        }
    }

    private fun configureTimerToExitApp() {
        timer = object :
            CountDownTimer(TIME_TO_ACCEPT_CLOSE_APP.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                if (closeAppFlag == 2) {
                    cancel()
                    finish()
                }
            }

            override fun onFinish() {
                closeAppFlag = 0
            }
        }
        timer.start()
    }

    companion object {
        private const val TIME_TO_ACCEPT_CLOSE_APP = 5 * 1000
        const val COUNT_DOWN_INTERVAL = 200
    }
}