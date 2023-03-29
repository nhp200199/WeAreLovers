package com.phucnguyen.lovereminder.ui.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.databinding.FragmentMainBinding
import com.phucnguyen.lovereminder.receiver.CoupleDateReceiver
import com.phucnguyen.lovereminder.ui.activity.BaseActivity
import com.phucnguyen.lovereminder.ui.activity.SettingActivity
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeThemeDialog
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeThemeDialog.ThemeDialogListener
import com.phucnguyen.lovereminder.ui.fragment.dialog.DialogFragment
import com.phucnguyen.lovereminder.ui.uiState.UserInfoUiState
import com.phucnguyen.lovereminder.viewmodel.MainFragmentViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MainFragment : Fragment(), DialogFragment.Listener, View.OnClickListener, ThemeDialogListener {
    interface SettingsListener {
        fun onBackgroundImageChanged(uri: Uri)
    }

    private var binding: FragmentMainBinding? = null
    private val viewModel: MainFragmentViewModel by viewModels()
    var height = 0
    var width = 0
    private var listener: SettingsListener? = null
    private lateinit var coupleDate: String
    override fun onDestroy() {
        Log.d("Tag", "Main Frag Destroyed")
        super.onDestroy()
    }

    override fun onStop() {
        Log.d("Tag", "Main Frag Stopped")
        super.onStop()
    }

    override fun onResume() {
        Log.d("Tag", "Main Frag Resumed")
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Tag", "Main Frag created")
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        Log.d("Tag", "Main Frag Destroyed View")
        super.onDestroyView()
        binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SettingsListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        binding = FragmentMainBinding.bind(v)
        connectViews(binding!!)
        val displayMetrics = DisplayMetrics()
        requireActivity() .windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        setHasOptionsMenu(true)
        val zoomin = AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
        binding!!.imgHeart.startAnimation(zoomin)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navigateSettingScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateSettingScreen() {
        startActivity(Intent(requireContext(), SettingActivity::class.java))
    }

    private fun showPopupChangeTheme() {
        val changeDateDialog = ChangeThemeDialog()
        changeDateDialog.setTargetFragment(this, 123)
        changeDateDialog.show(requireFragmentManager(), "ChangeThemeDialog")
    }

    private fun connectViews(binding: FragmentMainBinding) {
        binding.mainFragLinear.setOnClickListener(this)
        binding.tvYourFrName.setOnClickListener(this)
        binding.tvYourName.setOnClickListener(this)
        binding.tvDayCount.setOnClickListener(this)
        binding.friendProfileImage.setOnClickListener(this)
        binding.profileImage.setOnClickListener(this)
    }

    private fun setUserInfo(userInfoState: UserInfoUiState) {
        binding!!.tvYourName.text = userInfoState.yourName
        binding!!.tvYourFrName.text = userInfoState.yourFrName

        val calendar = Calendar.getInstance()
        var dateStartString = userInfoState.coupleDate
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateStart = simpleDateFormat.parse(dateStartString)
        val dateEndString = simpleDateFormat.format(calendar.time)
        val dateEnd = simpleDateFormat.parse(dateEndString)
        val diff = dateEnd.time - dateStart.time
        binding!!.tvDayCount.text =
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
                .toString()

        Glide.with(requireActivity()  )
            .load(userInfoState.yourImage)
            .into(binding!!.profileImage)
        Glide.with(requireActivity()  )
            .load(userInfoState.yourFrImage)
            .into(binding!!.friendProfileImage)
    }

    override fun applyChange(username: String) {
        if (viewModel.flag == 0) {
            viewModel.updateYourName(username)
        } else {
            viewModel.updateYourFrName(username)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_image -> {
                changePicture()
                viewModel.flag = 0
            }
            R.id.friend_profile_image -> {
                changePicture()
                viewModel.flag = 1
            }
            R.id.tv_yourName -> {
                viewModel.flag = 0
                showPopUpChangeName()
            }
            R.id.tv_yourFrName -> {
                viewModel.flag = 1
                showPopUpChangeName()
            }
            R.id.main_frag_linear -> showPopUpChangeDate()
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
        val datePickerDialog = DatePickerDialog(requireContext(),
            android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight,
            pickerListener,
            previousSelectedYear, previousSelectedMonth, previousSelectedDay
        )
        datePickerDialog.datePicker.maxDate = now.timeInMillis
        datePickerDialog.show()
    }

    private fun showPopUpChangeName() {
        val dialogFragment = DialogFragment()
        val bundle = Bundle()
        if (viewModel.flag == 0) bundle.putString(
            "name",
            binding!!.tvYourName.text.toString().trim { it <= ' ' }) else bundle.putString(
            "name",
            binding!!.tvYourFrName.text.toString().trim { it <= ' ' })
        dialogFragment.arguments = bundle
        dialogFragment.setTargetFragment(this@MainFragment, 1)
        dialogFragment.show(requireFragmentManager(), "custom dialog")
    }

    private fun changePicture() {
        val actionBarHeight = (activity as AppCompatActivity?)!!.supportActionBar!!
            .height
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityTitle("My Crop")
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setCropMenuCropButtonTitle("Done")
            .setAspectRatio(width, height - actionBarHeight)
            .setFixAspectRatio(true)
            .setRequestedSize(
                width,
                height - actionBarHeight,
                CropImageView.RequestSizeOptions.RESIZE_EXACT
            )
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (viewModel.flag == 0) {
                    viewModel.updateYourImage(result.uri.toString())
                } else if (viewModel.flag == 1) {
                    viewModel.updateYourFrImage(result.uri.toString())
                } else if (viewModel.flag == 2) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    try {
                        BitmapFactory.decodeStream(
                            requireActivity() .contentResolver.openInputStream(result.uri),
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
                    if (listener != null) {
                        listener!!.onBackgroundImageChanged(result.uri)
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(context, "Cropping failed: " + result.error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInfoUiStateFlow.collect {
                    coupleDate = it.coupleDate
                    setUserInfo(it)
                }
            }
        }
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
        val intent = Intent(requireActivity(), CoupleDateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
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

    override fun onThemeDialogChanged(themeId: Int) {
        var newThemeId = 0
        when (themeId) {
            R.color.amaranth -> newThemeId = R.style.AppThemeBase_Rose
            R.color.royal_blue -> newThemeId = R.style.AppThemeBase_Blue
        }
        val editor = requireActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            .edit()
        editor.putInt("theme_color", themeId).apply()
        (activity as BaseActivity?)!!.switchTheme(newThemeId)
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}