package com.rockcode.har;

import android.util.Log;

/**
 * Output log to Logcat
 */

class LogUtil {

    /**
     * logcat tag
     */
    private final static String TAG = "harlib";

    /**
     * log info
     * @param str
     */
    static void info(String str) {
        if(HarConfigs.isDebug()) {
            Log.i(TAG, str);
        }
    }

    /**
     * log error
     * @param str
     */
    static void err(String str) {
        if(HarConfigs.isDebug()) {
            Log.e(TAG, str);
        }
    }
}
