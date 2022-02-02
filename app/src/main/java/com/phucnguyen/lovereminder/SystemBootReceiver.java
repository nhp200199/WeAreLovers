package com.phucnguyen.lovereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class SystemBootReceiver extends BroadcastReceiver {
    private static final String TAG = SystemBootReceiver.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.i(TAG, String.format("System rebooted. Next alarm at: %d", calendar.getTimeInMillis()));

        Intent intentForPending = new Intent(context, CoupleDateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                intentForPending,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);
    }
}
