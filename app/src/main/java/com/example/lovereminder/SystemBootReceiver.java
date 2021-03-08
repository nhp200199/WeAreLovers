package com.example.lovereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SystemBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);

        Intent intentForPending = new Intent(context, CoupleDateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                intentForPending,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }
}
