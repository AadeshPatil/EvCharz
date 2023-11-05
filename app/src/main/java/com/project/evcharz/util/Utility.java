package com.project.evcharz.util;
import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public static String formatTime(int hour, String minute) {
        if (hour >= 0 && hour < 12) {
            return hour + " : " + minute + " AM";
        } else {
            if (hour != 12) {
                hour = hour - 12;
            }
            return hour + " : " + minute + " PM";
        }
    }

    public static long checkDuration( String startTimeFormat, String endTimeFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date startDate = simpleDateFormat.parse(startTimeFormat);
        Date endDate = simpleDateFormat.parse(endTimeFormat);

        long difference = endDate.getTime() - startDate.getTime();

        if (difference < 0) {
            Date dateMax = simpleDateFormat.parse("24:00");
            Date dateMin = simpleDateFormat.parse("00:00");
            difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
        }

        int hours = (int) (difference / (1000 * 60 * 60));
        int min = (int) ((difference - (1000 * 60 * 60 * hours)) / (1000 * 60));

        return hours != 0 ? hours * 60L : min;
    }
}
