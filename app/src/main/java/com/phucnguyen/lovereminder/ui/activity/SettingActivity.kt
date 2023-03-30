package com.phucnguyen.lovereminder.ui.activity

import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.ActivitySettingBinding
import com.phucnguyen.lovereminder.receiver.CoupleDateReceiver
import com.phucnguyen.lovereminder.ui.adapter.ColorThemesAdapter
import com.phucnguyen.lovereminder.ui.fragment.MainFragment
import com.phucnguyen.lovereminder.utils.hideKeyboard
import com.phucnguyen.lovereminder.viewmodel.SettingViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class SettingActivity : BaseActivity() {
    //TODO: research Flow to get rid of this declaration
    private var currentTheme: Int = R.style.AppThemeBase_Rose
    private lateinit var coupleDate: String
    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var adapter: ColorThemesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.tb.setNavigationOnClickListener {
            viewModel.changeAppTheme(currentTheme)
            finish()
        }

        setupListeners()

        setUpObservers()

        adapter = ColorThemesAdapter()
        adapter.setListener(object : ColorThemesAdapter.Listener {
            override fun onColorThemeSelected(themeColorId: Int) {
                viewModel.changeColorTheme(themeColorId)
            }

        })
        binding.rcvThemes.apply {
            adapter = this@SettingActivity.adapter
            layoutManager =
                LinearLayoutManager(this@SettingActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.coupleDateFlow.collect {
                    coupleDate = it
                    binding.tvActionCoupleDate.text = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedThemeFlow.collect {
                    currentTheme = it
                    applyTheme(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.previewColorThemesFlow.collect {
                    adapter.setThemesList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userNameFlow.collect {
                    binding.tvYourName.text = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userFriendNameFlow.collect {
                    binding.tvYourFriendName.text = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backgroundPictureFlow.collect {
                    Glide.with(binding.ivBackgroundPicture)
                        .load(it)
                        .into(binding.ivBackgroundPicture)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.yourAvatarFlow.collect {
                    Glide.with(binding.civYourAvatar)
                        .load(it)
                        .into(binding.civYourAvatar)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.yourFriendAvatarFlow.collect {
                    Glide.with(binding.civYourFriendAvatar)
                        .load(it)
                        .into(binding.civYourFriendAvatar)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEditingYourNameFlow.collect { isEditing ->
                    if (isEditing) {
                        binding.tvActionEditYourName.text =
                            getString(R.string.txt_action_done_edit_name)

                        binding.edtEditYourName.visibility = View.VISIBLE
                        binding.edtEditYourName.setText(binding.tvYourName.text)
                        binding.tvYourName.visibility = View.GONE
                    } else {
                        binding.tvActionEditYourName.text = getString(R.string.txt_action_edit_name)

                        binding.edtEditYourName.visibility = View.GONE
                        binding.tvYourName.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEditingYourFriendNameFlow.collect { isEditing ->
                    if (isEditing) {
                        binding.tvActionEditYourFriendName.text =
                            getString(R.string.txt_action_done_edit_name)

                        binding.edtEditYourFriendName.visibility = View.VISIBLE
                        binding.edtEditYourFriendName.setText(binding.tvYourFriendName.text)
                        binding.tvYourFriendName.visibility = View.GONE
                    } else {
                        binding.tvActionEditYourFriendName.text =
                            getString(R.string.txt_action_edit_name)

                        binding.edtEditYourFriendName.visibility = View.GONE
                        binding.tvYourFriendName.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showPopUpChangeDate() {
        val pickerListener = DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->
            val newCoupleDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            viewModel.updateCoupleDate(newCoupleDate)
            changeAlarm()
        }

        val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(coupleDate)
        val previousSelectedDay = calendar[Calendar.DAY_OF_MONTH]
        val previousSelectedMonth = calendar[Calendar.MONTH]
        val previousSelectedYear = calendar[Calendar.YEAR]


        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight,
            pickerListener,
            previousSelectedYear, previousSelectedMonth, previousSelectedDay
        )
        datePickerDialog.datePicker.maxDate = now.timeInMillis
        datePickerDialog.show()
    }

    private fun changeAlarm() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 9
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        Log.i(
            TAG,
            String.format("Couple data has been changed. Next alarm at: %d", calendar.timeInMillis)
        )
        val intent = Intent(this, CoupleDateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun applyTheme(themeId: Int) {
        val currentTheme = resources.newTheme()
        currentTheme.applyStyle(themeId, true)
        val typedValue = TypedValue()
        currentTheme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        val colorPrimary = typedValue.data

        binding.viewPreviewToolbar.background = ColorDrawable(colorPrimary)
        binding.imgHeart.imageTintList = ColorStateList(
            arrayOf(
                intArrayOf()
            ),
            intArrayOf(colorPrimary)
        )
        binding.civYourAvatarPreview.borderColor = colorPrimary
        binding.civYourFriendAvatarPreview.borderColor = colorPrimary
    }

    private fun setupListeners() {
        binding.tvActionCoupleDate.setOnClickListener {
            showPopUpChangeDate()
        }

        binding.tvActionEditYourName.setOnClickListener {
            if (!viewModel.isEditingYourName) {
                viewModel.startEditYourName()
            } else {
                hideKeyboard(this)
                viewModel.stopEditYourName()
                viewModel.changeYourName(binding.edtEditYourName.text.toString())
            }
        }

        binding.tvActionEditYourFriendName.setOnClickListener {
            if (!viewModel.isEditingYourFriendName) {
                viewModel.startEditYourFriendName()
            } else {
                hideKeyboard(this)
                viewModel.stopEditYourFriendName()
                viewModel.changeYourFriendName(binding.edtEditYourFriendName.text.toString())
            }
        }

        binding.civYourAvatar.setOnClickListener {
            viewModel.changeTarget = SettingViewModel.CHANGE_TARGET_YOU
            chooseAndCropImage()
        }

        binding.civYourFriendAvatar.setOnClickListener {
            viewModel.changeTarget = SettingViewModel.CHANGE_TARGET_YOUR_FRIEND
            chooseAndCropImage()
        }

        binding.ivBackgroundPicture.setOnClickListener {
            viewModel.changeTarget = SettingViewModel.CHANGE_TARGET_BACKGROUND_PICTURE
            chooseAndCropImage()
        }
    }

    private fun chooseAndCropImage() {
        val actionBarHeight = supportActionBar!!.height
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityTitle("My Crop")
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setCropMenuCropButtonTitle("Done")
//            .setAspectRatio(width, height - actionBarHeight)
//            .setFixAspectRatio(true)
//            .setRequestedSize(
//                width,
//                height - actionBarHeight,
//                CropImageView.RequestSizeOptions.RESIZE_EXACT
//            )
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (viewModel.changeTarget == SettingViewModel.CHANGE_TARGET_YOU) {
                    viewModel.changeYourAvatar(result.uri.toString())
                } else if (viewModel.changeTarget == SettingViewModel.CHANGE_TARGET_YOUR_FRIEND) {
                    viewModel.changeYourFriendAvatar(result.uri.toString())
                } else if (viewModel.changeTarget == SettingViewModel.CHANGE_TARGET_BACKGROUND_PICTURE) {
                    viewModel.changeBackgroundPicture(result.uri.toString())
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        val TAG: String = SettingActivity::class.java.simpleName
    }
}