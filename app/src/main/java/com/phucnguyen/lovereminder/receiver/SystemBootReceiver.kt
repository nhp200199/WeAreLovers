package com.phucnguyen.lovereminder.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*

class SystemBootReceiver : BroadcastReceiver() {
    private val TAG = SystemBootReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
         if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val timeForNotification = Calendar.getInstance()
            timeForNotification[Calendar.HOUR_OF_DAY] = 9
            timeForNotification[Calendar.MINUTE] = 0
            timeForNotification[Calendar.SECOND] = 0
            if (timeForNotification.before(Calendar.getInstance())) {
                timeForNotification.add(Calendar.DATE, 1)
            }
            Log.i(
                TAG,
                String.format(
                    "System rebooted. Next alarm at: %d",
                    timeForNotification.timeInMillis
                )
            )
            val intentForPending = Intent(context, CoupleDateReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intentForPending,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeForNotification.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeForNotification.timeInMillis,
                    pendingIntent
                )
            }
        }
    }
}