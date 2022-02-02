package com.phucnguyen.lovereminder;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

public class CoupleDateReceiverShould {
    @Test
    public void rescheduleAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String expectedScheduledDate = "02/02/2022 09:00:00";
        String actualScheduledDate = sdf.format(calendar.getTime());

        assertEquals(expectedScheduledDate, actualScheduledDate);
    }
}
