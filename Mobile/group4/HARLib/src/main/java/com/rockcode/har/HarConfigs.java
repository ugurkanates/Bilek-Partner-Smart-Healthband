package com.rockcode.har;

/**
 * HarLib Configs.
 */
public class HarConfigs {

    /**
     * debug log output
     */
    private static boolean mDebug = true;

    /**
     * for distinguish between different users
     */
    private static String mUserId = "0";

    /**
     * sensor collect time for one recognition cycle
     */
    private static int mSensorCollectTime = 5000;

    /**
     * sensor sample freq (times/second)
     */
    private static int mSampleFreq = 20;

    /**
     * is debug open
     * @return true: open, false: close
     */
    public static boolean isDebug() {
        return mDebug;
    }

    /**
     * set debug open
     * @param isDebug true: open, false: close
     */
    public static void setDebug(boolean isDebug) {
        mDebug = isDebug;
    }

    /**
     * get sensor collect time
     * @return time(ms)
     */
    public static int getSensorCollectTime() {
        return mSensorCollectTime;
    }

    /**
     * set sensor collect time
     * @param ms time(ms)
     */
    public static void setSensorCollectTime(int ms) {
        mSensorCollectTime = ms;
    }

    /**
     * get sample frequency
     * @return times per second
     */
    public static int getSampleFreq() {
        return mSampleFreq;
    }

    /**
     * set sample frequency
     * @param hz times per second
     */
    public static void setSampleFreq(int hz) {
        mSampleFreq = hz;
    }

    /**
     * get total sample number for one recognition cycle
     * @return sample number
     */
    public static int getSampleNumber() {
        return (mSensorCollectTime / 1000) * mSampleFreq;
    }

    /**
     * get least sample number for one recognition cycle
     * @return least sample number
     */
    public static int getLeastSampleNumber() {
        return (int) (0.5 * getSampleNumber());
    }

    /**
     * user id(can be any string), for distinguish between different users
     * @param userid
     */
    public static void setUserId(String userid) {
        mUserId = userid;
    }

    /**
     * get user id, for distinguish between different users
     * @return user id
     */
    public static String getUserId() {
        return mUserId;
    }
}
