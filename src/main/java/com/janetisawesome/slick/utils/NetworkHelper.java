package com.janetisawesome.slick.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * NetworkHelper utility class
 * Aggregate network calls here
 */
public class NetworkHelper {

    private static final String TAG = "NetworkHelper";

    // Do not instantiate
    private NetworkHelper() {};


    /**
     * isNetworkConnected - returns true if network is connected
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
