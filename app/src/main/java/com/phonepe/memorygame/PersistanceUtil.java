package com.phonepe.memorygame;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PersistanceUtil {
    private static final String LEVEL = "level";
    private static final String SCORE = "score";

    public static void setCurrentLevel(Context context, String level) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(LEVEL, level).apply();
    }

    public static String getCurrentLevel(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(LEVEL, "1");
    }

    public static void addScore(Context context, long score) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long currentScore = preferences.getLong(SCORE, 0);
        preferences.edit().putLong(SCORE, currentScore + score).apply();
    }

    public static long getCurrentScore(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(SCORE, 0);
    }

    public static void clearPreference(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().apply();
    }
}
