package com.phucnguyen.lovereminder.receiver

import androidx.annotation.RequiresApi
import android.os.Build
import android.content.Intent
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import java.util.*

class SystemBootReceiver : BroadcastReceiver() {
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        //TODO: filter action string + support other API different then android M

        val timeForNotification = Calendar.getInstance()
        timeForNotification[Calendar.HOUR_OF_DAY] = 9
        timeForNotification[Calendar.MINUTE] = 0
        timeForNotification[Calendar.SECOND] = 0
        if (timeForNotification.before(Calendar.getInstance())) {
            timeForNotification.add(Calendar.DATE, 1)
        }
        Log.i(TAG, String.format("System rebooted. Next alarm at: %d", timeForNotification.timeInMillis))
        val intentForPending = Intent(context, CoupleDateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intentForPending,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeForNotification.timeInMillis,
            pendingIntent
        )
    }

    companion object {
        private val TAG = SystemBootReceiver::class.java.simpleName
    }
}