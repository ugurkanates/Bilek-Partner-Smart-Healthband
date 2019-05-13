package com.rockcode.har;

import java.util.Date;

/**
 * Raw Sensor Data
 */
public class RawData {

	/**
	 * timestamp(already trans sensor event timestamp to unix timestamp)
	 */
	public long mTimeStamp;

	/**
	 * X axis value
	 */
	public float mXAxisValue;

	/**
	 * y axis value
	 */
	public float mYAxisValue;

	/**
	 * z axis value
	 */
	public float mZAxisValue;

	/**
	 * target label activity
	 */
	public String mActivity = HumanActivity.ACTIVITY_NOLABEL;

	/**
	 * Raw sensor data
	 * @param activity target label activity
	 * @param timestamp sensor event timestamp
	 * @param x The X axis value
	 * @param y The X axis value
	 * @param z The X axis value
	 */
	public RawData(String activity, long timestamp, float x, float y, float z) {
		this.mActivity = activity;
		// FIXME
		// these two method trans sensor event timestamp to unix timestamp are not the same for all phone
//		this.mTimeStamp = (new Date()).getTime() + (timestamp - System.nanoTime()) / 1000000L;
//		this.mTimeStamp = System.currentTimeMillis() + ((timestamp - SystemClock.elapsedRealtimeNanos())/1000000L);;
		// use current time
		this.mTimeStamp = System.currentTimeMillis();
		this.mXAxisValue = x;
		this.mYAxisValue = y;
		this.mZAxisValue = z;
	}

	/**
	 * Raw sensor data
	 * @param timestamp sensor event timestamp
	 * @param x The X axis value
	 * @param y The X axis value
	 * @param z The X axis value
	 */
	public RawData(long timestamp, float x, float y, float z) {
		//trans to unix timestamp
		this.mTimeStamp = (new Date()).getTime() + (timestamp - System.nanoTime()) / 1000000L;
		this.mXAxisValue = x;
		this.mYAxisValue = y;
		this.mZAxisValue = z;
	}

	/**
	 * get RawData String
	 * @return "[userid],[activity],[timestamp],[x],[y],[z];\n"
	 */
	public String toString() {
		String str = "";
		str += HarConfigs.getUserId() + ",";
		str += mActivity + ",";
		str += mTimeStamp + ",";
		str += mXAxisValue + ",";
		str += mYAxisValue + ",";
		str += mZAxisValue + ";\n";
		return str;
	}

	/**
	 * get absolute diff time of another RawData
	 * @paramrawData another RawData to diff
	 * @return abs diff time
	 */
	public long diffTimeAbs(RawData rawData) {
			return Math.abs(rawData.mTimeStamp - this.mTimeStamp);
	}
}
