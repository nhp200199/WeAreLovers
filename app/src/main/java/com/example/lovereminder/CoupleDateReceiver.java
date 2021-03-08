package com.example.lovereminder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class CoupleDateReceiver extends BroadcastReceiver {

    private static final String[] TITLES = {
        "Chú ý chú ý!!!",
            "Hello, bạn biết gì chưa?"
    };
    private static final String[] CONTENTS = {
        "Tèn ten, thêm 1 tháng là thêm tình cảm nồng nàn nhé!",
            "Chúc mừng chúc mừng, tình cảm lứa đôi mãnh liệt như 2 bạn thật đáng nể"
    };
    private int notificationId;
    private SharedPreferences mSharedPreferences;


    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        int coupleDay = 0;
        int coupleMonth = -1;
        mSharedPreferences = context.getSharedPreferences("userInfor", MODE_PRIVATE);
        String coupleDateString = mSharedPreferences.getString("date", null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObject = null;
        try {
            dateObject = simpleDateFormat.parse(coupleDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateObject!=null){
            coupleDay = dateObject.getDate();
            coupleMonth = dateObject.getMonth();
        }

        if (currentDay == coupleDay - 1 && currentMonth != coupleMonth)
            showNotification(context);
    }

    private void showNotification(Context context) {
        Intent intentActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentActivity,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                BaseApplication.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getRandomString(TITLES))
                .setContentText(getRandomString(CONTENTS))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    private String getRandomString(String[] listOfStrings) {
        Random random = new Random();
        return listOfStrings[random.nextInt(listOfStrings.length)];
    }
}