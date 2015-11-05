package com.janetisawesome.slick.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Android utilities helper class
 * Put all Android-specific function calls here,
 * e.g.
 * startActivity
 * enabling packages,
 * searching for installed components, etc
 *
 */
public class AndroidHelper {

    // Logging Tag
    private static final String TAG = "AndroidHelper";

    // Do not instantiate
    private AndroidHelper() {}


    /**
     * Start an activity for the given intent. Returns false if activity
     * not found or unexpected error.
     *
     * @param context context non-null
     * @param intent  intent to start an activity
     * @return true if started, false if activity not found or unexpected error.
     */
    public static final boolean startActivity(Context context, Intent intent) {

        if (intent != null) {
            try {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                return true;
            }catch(ActivityNotFoundException ex) {
                Log.e(TAG, " Activity not found for intent: " + intent, ex);
            }catch(Throwable ex) {
                Log.e(TAG, " Unable to start activity for intent: " + intent, ex);
            }
        }
        return false;
    }

}
