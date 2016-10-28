package com.example.android.cinefile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Hendercine on 10/19/16.
 */

public class Utility {
    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key), (context.getString(R.string.pref_sort_default)));
    }

    public static boolean isPopular(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key), (context.getString(R.string.pref_sort_default))).equals(context.getString(R.string.pref_sort_popular));
    }

}
