package com.janetisawesome.slick.utils;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences Helper
 *
 * Aggregate all the preference names and get/set here,
 * So we can keep track of all the preferences being stored and retrieved
 * by this application
 */

public class PrefsHelper {

    /**
     * SharedPreferences file name
     */
    private static final String PREFS_NAME = "com.janetisawesome.preferences";

    // Do not instantiate
    private PrefsHelper() { }

    /////////////////////////////////////////////////////////
    // Preference Keys
    public static final String PREF_MEMBER_LIST_KEY = "pref_member_list_key";


    /**
     * Returns the SharedPreferences for UI.
     *
     * @param context
     * @return
     */
    public static final SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Set a String Preference
     * @param context
     * @param prefName
     * @param prefValue
     */
    public static void setStringPref(Context context, String prefName, String prefValue) {
        SharedPreferences.Editor prefs = getPreferences(context).edit();
        prefs.putString(prefName, prefValue);
        prefs.apply();
    }

    /**
     * Get a String Preference
     * @param context
     * @param prefName
     * @param defaultValue
     * @return
     */
    public static String getStringPref(Context context, String prefName, String defaultValue) {
        return getPreferences(context).getString(prefName, defaultValue);
    }
}