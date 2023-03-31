package com.phucnguyen.lovereminder.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.phucnguyen.lovereminder.INITIAL_AD_COUNTDOWN
import com.phucnguyen.lovereminder.PREF_COUNT_DOWN_TO_SHOW_AD
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.SHARE_PREF_ADMOB
import com.phucnguyen.lovereminder.databinding.ActivityCreateDiaryBinding
import com.phucnguyen.lovereminder.model.Diary
import com.phucnguyen.lovereminder.utils.hideKeyboard
import com.phucnguyen.lovereminder.viewmodel.CreateDiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CreateDiaryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCreateDiaryBinding
    private val viewModel: CreateDiaryViewModel by viewModels()
    private var interstitialAd: InterstitialAd? = null
    private lateinit var admobSharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDiaryBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { view: View? -> onBackPressed() }

        admobSharedPref = getSharedPreferences(SHARE_PREF_ADMOB, AppCompatActivity.MODE_PRIVATE)

        viewModel.getBackgroundImage()?.apply {
            Glide.with(this@CreateDiaryActivity)
                .load(this@apply)
                .into(binding.imgBackground)
        }
        binding.btnSave.setOnClickListener(this)
        binding.edtDiary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = binding.edtDiary.text.toString().trim { it <= ' ' }
                binding.btnSave.isEnabled = string.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        loadAd()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_save -> saveDiary()
        }
    }

    private fun saveDiary() {
        val calendar = Calendar.getInstance()
        val content = binding.edtDiary.text.toString()
        val diary = Diary(0, calendar.timeInMillis, content)

        lifecycleScope.launch {
            val diaryId = viewModel.createDiary(diary)
            if (diaryId > 0 ) {
                Toast.makeText(this@CreateDiaryActivity, getString(R.string.toast_msg_diary_created), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CreateDiaryActivity, getString(R.string.toast_msg_fail_create_diary), Toast.LENGTH_SHORT).show()
            }
            binding.edtDiary.setText("")
            hideKeyboard(this@CreateDiaryActivity)

            decreaseCountdownAndCheckShowAd()
        }
    }

    private fun decreaseCountdownAndCheckShowAd() {
        var countdown = admobSharedPref.getInt(PREF_COUNT_DOWN_TO_SHOW_AD, INITIAL_AD_COUNTDOWN)
        admobSharedPref.edit().putInt(PREF_COUNT_DOWN_TO_SHOW_AD, --countdown).apply()

        if (countdown <= 0) {
            showInterstitial()
        }
    }

    override fun onBackPressed() {
        if (binding.edtDiary.text.toString() == "") {
            super@CreateDiaryActivity.onBackPressed()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Chưa lưu đoạn nhật kí kìa ấy ơi. Bạn có muốn lưu lại không?")
                .setPositiveButton("Có") { dialog, which -> saveDiary() }
                .setNegativeButton("Không") { dialog, which ->
                    dialog.cancel()
                    finish()
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            getString(R.string.interstitial_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                }
            }
        )
    }

    private fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        resetAdmobCountdown()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e(TAG, "Ad failed to show.")

                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        loadAd()
                    }
                }
            interstitialAd?.show(this)
        } else {
            Log.e(TAG, "Ad wasn't loaded.")
        }
    }

    private fun resetAdmobCountdown() {
        val newCountdown = randomCountdown()
        admobSharedPref.edit().putInt(PREF_COUNT_DOWN_TO_SHOW_AD, newCountdown).apply()
    }

    private fun randomCountdown(): Int {
        val nextCountdown = Random().nextInt(3)
        Log.d(TAG, "Ad will be showed again after " + nextCountdown + " times create diary")
        return nextCountdown
    }

    companion object {
        val TAG: String = CreateDiaryActivity::class.java.simpleName
    }
}