package com.phucnguyen.lovereminder.ui.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.phucnguyen.lovereminder.R
import com.phucnguyen.lovereminder.databinding.FragmentMainBinding
import com.phucnguyen.lovereminder.receiver.CoupleDateReceiver
import com.phucnguyen.lovereminder.ui.activity.BaseActivity
import com.phucnguyen.lovereminder.ui.fragment.MainFragment
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeDateDialog
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeThemeDialog
import com.phucnguyen.lovereminder.ui.fragment.dialog.ChangeThemeDialog.ThemeDialogListener
import com.phucnguyen.lovereminder.ui.fragment.dialog.DialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.FileNotFoundException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), DialogFragment.Listener, View.OnClickListener,
    ChangeDateDialog.Listener, ThemeDialogListener {
    interface SettingsListener {
        fun onBackgroundImageChanged(uri: Uri)
    }

    private var flag // to distinguish you from your friend
            = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var binding: FragmentMainBinding? = null
    var height = 0
    var width = 0
    private var listener: SettingsListener? = null
    private var ownerName: String? = null
    private var partnerName: String? = null
    private var coupleDate: String? = null
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
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        setHasOptionsMenu(true)
        sharedPreferences = activity!!.getSharedPreferences("userInfor", Context.MODE_PRIVATE)
        ownerName = sharedPreferences.getString("yourName", DEFAULT_NAME)
        partnerName = sharedPreferences.getString("yourFrName", DEFAULT_NAME)
        coupleDate = sharedPreferences.getString("date", DEFAULT_COUPLE_DATE)
        loadUserData()
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
            R.id.action_change_background -> {
                flag = 2
                changePicture()
                true
            }
            R.id.action_change_date -> {
                showPopUpChangeDate()
                true
            }
            R.id.action_change_theme -> {
                showPopupChangeTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupChangeTheme() {
        val changeDateDialog = ChangeThemeDialog()
        changeDateDialog.setTargetFragment(this, 123)
        changeDateDialog.show(fragmentManager!!, "ChangeThemeDialog")
    }

    private fun loadUserData() {
        if (sharedPreferences!!.getString("yourImg", "") !== "") loadUserImg()
        try {
            setInfor(ownerName, partnerName, coupleDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun connectViews(binding: FragmentMainBinding) {
        binding.mainFragLinear.setOnClickListener(this)
        binding.tvYourFrName.setOnClickListener(this)
        binding.tvYourName.setOnClickListener(this)
        binding.tvDayCount.setOnClickListener(this)
        binding.friendProfileImage.setOnClickListener(this)
        binding.profileImage.setOnClickListener(this)
    }

    private fun loadUserImg() {
        val yourImg = sharedPreferences!!.getString("yourImg", "")
        val yourFrImg = sharedPreferences!!.getString("yourFrImg", "")
        val yourUri = Uri.parse(yourImg)
        val yourFrUri = Uri.parse(yourFrImg)
        Glide.with(activity!!)
            .load(yourUri)
            .into(binding!!.profileImage)
        Glide.with(activity!!)
            .load(yourFrUri)
            .into(binding!!.friendProfileImage)
    }

    @Throws(ParseException::class)
    private fun setInfor(yourName: String?, yourFrName: String?, Days: String?) {
        val calendar = Calendar.getInstance()
        var date_start_string: String? = ""
        date_start_string = Days
        binding!!.tvYourName.text = yourName
        binding!!.tvYourFrName.text = yourFrName
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date_start = simpleDateFormat.parse(date_start_string)
        val date_end_string = simpleDateFormat.format(calendar.time)
        val date_end = simpleDateFormat.parse(date_end_string)
        val diff = date_end.time - date_start.time
        binding!!.tvDayCount.text =
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
                .toString()
    }

    override fun ApplyChange(username: String?) {
        val editor = sharedPreferences!!.edit()
        if (flag == 0) {
            binding!!.tvYourName.text = username
            editor.putString("yourName", username)
        } else {
            binding!!.tvYourFrName.text = username
            editor.putString("yourFrName", username)
        }
        editor.apply()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_image -> {
                changePicture()
                flag = 0
            }
            R.id.friend_profile_image -> {
                changePicture()
                flag = 1
            }
            R.id.tv_yourName -> {
                flag = 0
                ShowPopUpChangeName()
            }
            R.id.tv_yourFrName -> {
                flag = 1
                ShowPopUpChangeName()
            }
            R.id.main_frag_linear -> showPopUpChangeDate()
        }
    }

    private fun showPopUpChangeDate() {
        val changeDateDialog = ChangeDateDialog()
        changeDateDialog.setTargetFragment(this@MainFragment, 2)
        changeDateDialog.show(fragmentManager!!, "custom")
    }

    private fun ShowPopUpChangeName() {
        val dialogFragment = DialogFragment()
        val bundle = Bundle()
        if (flag == 0) bundle.putString(
            "name",
            binding!!.tvYourName.text.toString().trim { it <= ' ' }) else bundle.putString(
            "name",
            binding!!.tvYourFrName.text.toString().trim { it <= ' ' })
        dialogFragment.arguments = bundle
        dialogFragment.setTargetFragment(this@MainFragment, 1)
        dialogFragment.show(fragmentManager!!, "custom dialog")
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
            .start(context!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (flag == 0) {
                    binding!!.profileImage.setImageURI(result.uri)
                    val editor = sharedPreferences!!.edit()
                    editor.putString("yourImg", result.uri.toString())
                    editor.apply()
                } else if (flag == 1) {
                    binding!!.friendProfileImage.setImageURI(result.uri)
                    val editor = sharedPreferences!!.edit()
                    editor.putString("yourFrImg", result.uri.toString())
                    editor.apply()
                } else if (flag == 2) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    try {
                        BitmapFactory.decodeStream(
                            activity!!.contentResolver.openInputStream(result.uri),
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun ApplyDateChange(date: String?) {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var date_start: Date? = null
        try {
            date_start = simpleDateFormat.parse(date)
            val date_end_string = simpleDateFormat.format(calendar.time)
            val date_end = simpleDateFormat.parse(date_end_string)
            val diff = date_end.time - date_start.time
            binding!!.tvDayCount.text =
                TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString()
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(activity, "Nhập sai định dạng", Toast.LENGTH_SHORT).show()
        }
        val editor = sharedPreferences!!.edit()
        editor.putString("date", date)
        editor.apply()

        //change the alarm
        changeAlarm()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    override fun onThemeDialogChanged(themeId: Int) {
        var newThemeId = 0
        when (themeId) {
            R.color.colorPrimary -> newThemeId = R.style.AppTheme
            R.color.blue -> newThemeId = R.style.AppTheme_Blue
        }
        val editor = activity!!.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            .edit()
        editor.putInt("theme_color", themeId).apply()
        (activity as BaseActivity?)!!.switchTheme(newThemeId)
    }

    companion object {
        const val DEFAULT_NAME = "Không xác định"
        const val DEFAULT_COUPLE_DATE = "26/12/1965"
        private val TAG = MainFragment::class.java.simpleName
    }
}