package com.rockcode.har.demo.base;

import android.location.Location;

import com.rockcode.har.HumanActivity;

public class UserActivityInfo extends HumanActivity {

	Location mLocation;

	public double mLatitude;
	public double mLongitude;
	public long mGpsTime;

	public UserActivityInfo(long startTime, String activity, long locationTime,
	                        double latitude, double longitude) {
		super(startTime, activity);
		mGpsTime = locationTime;
		mLatitude = latitude;
		mLongitude = longitude;
	}
}
