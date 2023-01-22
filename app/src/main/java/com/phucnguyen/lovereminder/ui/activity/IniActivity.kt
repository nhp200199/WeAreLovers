package com.phucnguyen.lovereminder.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.content.SharedPreferences
import android.os.Bundle
import com.phucnguyen.lovereminder.R
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
import com.phucnguyen.lovereminder.databinding.ActivityIniBinding
import java.util.*

class IniActivity : AppCompatActivity(), View.OnClickListener {
    private var edt_yourName: EditText? = null
    private var edt_yourFrName: EditText? = null
    private var edt_date: TextView? = null
    private var btn_confirm: Button? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIniBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        showDialog()
        connectViews(binding)
        sharedPreferences = getSharedPreferences("userInfor", MODE_PRIVATE)
        userPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        userPreferences.edit()
            .putInt("theme_color", R.color.colorPrimary)
            .apply()
        btn_confirm!!.isEnabled = false
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val date = edt_date!!.text.toString().trim { it <= ' ' }
                val yourName = edt_yourName!!.text.toString().trim { it <= ' ' }
                val yourFrName = edt_yourFrName!!.text.toString().trim { it <= ' ' }
                if (date == "Nhấn để chọn" || yourFrName.isEmpty() || yourName.isEmpty()) {
                    btn_confirm!!.isEnabled = false
                } else btn_confirm!!.isEnabled = true
            }

            override fun afterTextChanged(s: Editable) {}
        }
        edt_date!!.addTextChangedListener(textWatcher)
        edt_yourFrName!!.addTextChangedListener(textWatcher)
        edt_yourName!!.addTextChangedListener(textWatcher)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Hehe, khi em vào tới được đây rồi thì có nghĩa mình đã chính thức quen nhau được 1 năm rồi đó bé lùn tịt :))))")
            .setPositiveButton("I love you") { dialog, which -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun connectViews(binding: ActivityIniBinding) {
        edt_yourName = binding.edtYourName
        edt_yourFrName = binding.edtYourFrName
        edt_date = binding.edtDate
        btn_confirm = binding.btnConfirm
        btn_confirm!!.setOnClickListener(this)
        edt_date!!.setOnClickListener(this)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun collectInfor() {
        val editor = sharedPreferences!!.edit()
        editor.putString("yourName", edt_yourName!!.text.toString())
        editor.putString("yourFrName", edt_yourFrName!!.text.toString())
        editor.putString("date", edt_date!!.text.toString())
        editor.apply()

        //setting the alarm
        setAlarm()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        //persist alarm when system restarts
        val receiver = ComponentName(this, SystemBootReceiver::class.java)
        val pm = this.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                edt_date!!.text = date
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    companion object {
        private val TAG = IniActivity::class.java.simpleName
    }
}