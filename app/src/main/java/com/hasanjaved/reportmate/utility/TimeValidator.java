package com.hasanjaved.reportmate.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class TimeValidator {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_TIME_LIMIT = "time_limit_seconds";

    // Save current time and time limit (in seconds)
    public static void saveStartTime(Context context, Long timeLimitInSeconds) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long currentTimeMillis = System.currentTimeMillis();
        editor.putLong(KEY_START_TIME, currentTimeMillis);
        editor.putLong(KEY_TIME_LIMIT, timeLimitInSeconds);
        editor.apply();
    }

    // Check if the time limit has expired
    public static boolean isTimeValid(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long savedStartTime = prefs.getLong(KEY_START_TIME, -1);
        long timeLimitInSeconds = prefs.getLong(KEY_TIME_LIMIT, -1);

        if (savedStartTime == -1 || timeLimitInSeconds == -1) {
            return false; // No saved data
        }

        long elapsedSeconds = (System.currentTimeMillis() - savedStartTime) / 1000;

        return elapsedSeconds <= timeLimitInSeconds;
    }
}


