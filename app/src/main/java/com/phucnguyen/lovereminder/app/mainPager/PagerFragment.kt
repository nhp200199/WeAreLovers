package com.phucnguyen.lovereminder.app.mainPager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.core.base.presentation.BaseFragment
import com.phucnguyen.lovereminder.databinding.FragmentPagerBinding
import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.MainFragment
import com.phucnguyen.lovereminder.feature.couple.viewer.presentation.imageCropping.ImageCroppingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PagerFragment : BaseFragment<FragmentPagerBinding>() {
    private val viewModel: PagerViewModel by viewModels()

    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth)
        }

    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeMobileAdsSdk()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    override fun getClassTag(): String {
        return PagerFragment::class.java.simpleName
    }

    override fun getViewBindingClass(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPagerBinding {
        return FragmentPagerBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbar.tb)
            this.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        setupAdView()
        setUpViewPager()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backgroundImageFlow.collect {
                    Glide.with(requireContext())
                        .load(it)
                        .into(binding.imgBackground)
                }
            }
        }
    }

    override fun setViewListener() {
    }

    private fun initializeMobileAdsSdk() {
        // Initialize the Mobile Ads SDK with an AdMob App ID.
        MobileAds.initialize(requireContext()) {}

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(
                listOf(
                    "AC2FDCECBCC0ADA1D187ED08618252FD",
                    "41C49D16BFE9FC2EF7D54549D51BCB47" //pixel 5
                )
            ).build()
        )
    }

    private fun setupAdView() {
        adView = AdView(requireContext())
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                binding.adViewContainer.visibility = View.VISIBLE
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

        binding.adViewContainer.removeAllViews()
        binding.adViewContainer.addView(adView)
        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }
    }

    private fun loadBanner() {
        adView.adUnitId = getString(R.string.banner_ad_unit_id)

        adView.setAdSize(adSize)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    private fun setUpViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        binding.pager.adapter = sectionsPagerAdapter
//        binding.tlSwipe.setupWithViewPager(binding.pager)
//
//        //disable click on tab layout
//        for (v in binding.tlSwipe.touchables) {
//            v.isEnabled = false
//        }
    }

    private class SectionsPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(
        fm,
    ) {
        override fun getItemCount(): Int {
            return NUMBER_OF_PAGES
        }
        override fun createFragment(position: Int): Fragment {
            return MainFragment()
        }

        companion object {
            private const val NUMBER_OF_PAGES = 1
        }
    }
}