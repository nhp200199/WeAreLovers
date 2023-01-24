package com.phucnguyen.lovereminder.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import androidx.annotation.RequiresApi
import android.os.Build
import com.phucnguyen.lovereminder.ui.activity.IniActivity
import android.content.Intent
import com.phucnguyen.lovereminder.receiver.CoupleDateReceiver
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.ComponentName
import com.phucnguyen.lovereminder.receiver.SystemBootReceiver
import android.content.pm.PackageManager
import com.phucnguyen.lovereminder.ui.activity.MainActivity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.phucnguyen.lovereminder.*
import com.phucnguyen.lovereminder.databinding.ActivityIniBinding
import java.util.*

class IniActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreferences: SharedPreferences
    private lateinit var binding: ActivityIniBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showDialog()
        connectViews(binding)
        sharedPreferences = getSharedPreferences(SHARE_PREF_USER_INFO, MODE_PRIVATE)
        userPreferences = getSharedPreferences(SHARE_PREF_USER_PREFERENCE, MODE_PRIVATE)
        userPreferences.edit()
            .putInt(PREF_THEME_COLOR, R.color.colorPrimary)
            .apply()
        binding.btnConfirm.isEnabled = false
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val date = binding.edtDate.text.toString().trim { it <= ' ' }
                val yourName = binding.edtYourName.text.toString().trim { it <= ' ' }
                val yourFrName = binding.edtYourFrName.text.toString().trim { it <= ' ' }
                binding.btnConfirm.isEnabled = !(date == "Nhấn để chọn" || yourFrName.isEmpty() || yourName.isEmpty())
            }

            override fun afterTextChanged(s: Editable) {}
        }
        binding.edtDate.addTextChangedListener(textWatcher)
        binding.edtYourFrName.addTextChangedListener(textWatcher)
        binding.edtYourName.addTextChangedListener(textWatcher)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Hehe, khi em vào tới được đây rồi thì có nghĩa mình đã chính thức quen nhau được 1 năm rồi đó bé lùn tịt :))))")
            .setPositiveButton("I love you") { dialog, which -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun connectViews(binding: ActivityIniBinding) {
        binding.btnConfirm.setOnClickListener(this)
        binding.edtDate.setOnClickListener(this)
    }

    private fun collectInfor() {
        val editor = sharedPreferences.edit()
        editor.putString(PREF_YOUR_NAME, binding.edtYourName.text.toString())
        editor.putString(PREF_YOUR_FRIEND_NAME, binding.edtYourFrName.text.toString())
        editor.putString(PREF_COUPLE_DATE, binding.edtDate.text.toString())
        editor.apply()

        //setting the alarm
        setAlarm()
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 9
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        Log.i(
            TAG,
            String.format("Couple date has been set. Next alarm at: %d", calendar.timeInMillis)
        )
        val intent = Intent(this, CoupleDateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
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

        //persist alarm when system restarts
        val receiver = ComponentName(this, SystemBootReceiver::class.java)
        val pm = this.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_confirm -> {
                collectInfor()
                startActivity(Intent(this@IniActivity, MainActivity::class.java))
                finish()
            }
            R.id.edt_date -> showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            android.R.style.ThemeOverlay_DeviceDefault_Accent_DayNight,
            { view, year, month, dayOfMonth -> //because the month is counted from 0
                var month = month
                month = month + 1
                //TODO: reformat the text String.format
                val date = "$dayOfMonth/$month/$year"
                binding.edtDate.text = date
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    companion object {
        private val TAG = IniActivity::class.java.simpleName
    }
}