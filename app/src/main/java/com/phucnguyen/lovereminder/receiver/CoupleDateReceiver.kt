package com.phucnguyen.lovereminder.receiver

import android.content.SharedPreferences
import android.os.Build
import android.content.Intent
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.phucnguyen.lovereminder.ui.activity.MainActivity
import com.phucnguyen.lovereminder.BaseApplication
import com.phucnguyen.lovereminder.R
import androidx.core.app.NotificationManagerCompat
import com.phucnguyen.lovereminder.PREF_COUPLE_DATE
import com.phucnguyen.lovereminder.SHARE_PREF_USER_INFO
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CoupleDateReceiver : BroadcastReceiver() {
    private val _tag = CoupleDateReceiver::class.java.simpleName
    private val _titles = arrayOf(
        "Chú ý chú ý!!!",
        "Hello, bạn biết gì chưa?"
    )
    private val _contents = arrayOf(
        "Tèn ten, thêm 1 tháng là thêm tình cảm nồng nàn nhé!",
        "Chúc mừng chúc mừng, tình cảm lứa đôi mãnh liệt như 2 bạn thật đáng nể"
    )
    private val notificationId = 0
    private lateinit var mSharedPreferences: SharedPreferences

      override fun onReceive(context: Context, intent: Intent) {
        Log.i(_tag, "RECEIVED MESSAGE SCHEDULED FROM ALARM")
        rescheduleAlarm(context)
        checkShowNotification(context)
    }

    private fun checkShowNotification(context: Context) {
        val calendar = Calendar.getInstance()
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        val currentMonth = calendar[Calendar.MONTH]
        var coupleDay = 0
        var coupleMonth = -1
        mSharedPreferences = context.getSharedPreferences(SHARE_PREF_USER_INFO, Context.MODE_PRIVATE)
        val coupleDateString = mSharedPreferences.getString(PREF_COUPLE_DATE, null)
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var dateObject: Date? = null
        try {
            dateObject = simpleDateFormat.parse(coupleDateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (dateObject != null) {
            coupleDay = dateObject.date
            coupleMonth = dateObject.month
        }
        Log.i(_tag, "current date: $currentDay-${currentMonth + 1}")
        Log.i(_tag, "couple date: $coupleDay-${coupleMonth + 1}")
        if (currentDay == coupleDay - 1 && currentMonth != coupleMonth) {
            //TODO: notify on exact date and different notification when current month = couple month (1 year)
            showNotification(context, testing = false)
        } else {
            showNotification(context, testing = true)
        }
    }

    private fun rescheduleAlarm(context: Context) {
        val nextAlarm = Calendar.getInstance()
        nextAlarm[Calendar.DAY_OF_MONTH] = nextAlarm[Calendar.DAY_OF_MONTH] + 1
        nextAlarm[Calendar.HOUR_OF_DAY] = 9
        nextAlarm[Calendar.MINUTE] = 0
        nextAlarm[Calendar.SECOND] = 0
        Log.i(
            _tag,
            String.format("Couple date has been recycled. Next alarm at: %d", nextAlarm.timeInMillis)
        )

        val rescheduledIntent = Intent(context, CoupleDateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            rescheduledIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarm.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                nextAlarm.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun showNotification(context: Context, testing: Boolean) {
        val intentActivity = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intentActivity, 0)
        val builder = NotificationCompat.Builder(
            context,
            BaseApplication.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getRandomString(_titles))
            .setContentText(if (testing) "testing" else getRandomString(_contents))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build())
        Log.i("Receiver", "NOTIFICATION CREATED")
    }

    private fun getRandomString(listOfStrings: Array<String>): String {
        val random = Random()
        return listOfStrings[random.nextInt(listOfStrings.size)]
    }
}