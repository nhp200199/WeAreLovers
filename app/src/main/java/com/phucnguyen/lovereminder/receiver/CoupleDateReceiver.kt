package com.phucnguyen.lovereminder.receiver

import android.content.SharedPreferences
import androidx.annotation.RequiresApi
import android.os.Build
import android.content.Intent
import com.phucnguyen.lovereminder.receiver.CoupleDateReceiver
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CoupleDateReceiver : BroadcastReceiver() {
    private val notificationId = 0
    private var mSharedPreferences: SharedPreferences? = null
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "RECEIVED MESSAGE SCHEDULED FROM ALARM")
        //reschedule the alarm
        rescheduleAlarm(context)
        val calendar = Calendar.getInstance()
        val currentDay = calendar[Calendar.DAY_OF_MONTH]
        val currentMonth = calendar[Calendar.MONTH]
        var coupleDay = 0
        var coupleMonth = -1
        mSharedPreferences = context.getSharedPreferences("userInfor", Context.MODE_PRIVATE)
        val coupleDateString = mSharedPreferences!!.getString("date", null)
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
        if (currentDay == coupleDay - 1 && currentMonth != coupleMonth) showNotification(context) else showNotification(
            context,
            "Not yet"
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun rescheduleAlarm(context: Context) {
        val calendar = nextAlarm
        Log.i(
            TAG,
            String.format("Couple date has been recycled. Next alarm at: %d", calendar.timeInMillis)
        )
        val rescheduledIntent = Intent(context, CoupleDateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            rescheduledIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private val nextAlarm: Calendar
        private get() {
            val calendar = Calendar.getInstance()
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
            calendar[Calendar.HOUR_OF_DAY] = 9
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            return calendar
        }

    private fun showNotification(context: Context) {
        val intentActivity = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intentActivity, 0)
        val builder = NotificationCompat.Builder(
            context,
            BaseApplication.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getRandomString(TITLES))
            .setContentText(getRandomString(CONTENTS))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build())
        Log.i("Receiver", "NOTIFICATION CREATED")
    }

    private fun showNotification(context: Context, content: String) {
        val intentActivity = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intentActivity, 0)
        val builder = NotificationCompat.Builder(
            context,
            BaseApplication.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getRandomString(TITLES))
            .setContentText(content)
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

    companion object {
        private val TITLES = arrayOf(
            "Chú ý chú ý!!!",
            "Hello, bạn biết gì chưa?"
        )
        private val CONTENTS = arrayOf(
            "Tèn ten, thêm 1 tháng là thêm tình cảm nồng nàn nhé!",
            "Chúc mừng chúc mừng, tình cảm lứa đôi mãnh liệt như 2 bạn thật đáng nể"
        )
        private val TAG = CoupleDateReceiver::class.java.simpleName
    }
}