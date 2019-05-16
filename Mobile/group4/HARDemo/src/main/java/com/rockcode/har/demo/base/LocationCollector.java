package com.rockcode.har.demo.base;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.rockcode.har.demo.util.LogUtil;
import com.rockcode.har.demo.util.StrUtil;

public class LocationCollector {

	private LocationManager mLocationManager;

	private LocationListener mGPSLocationListener;
	private LocationListener mNetworkLocationListener;
	private boolean mIsListenGPSLocation = false;
	private boolean mIsListenNetWorkLocation = false;
	private boolean mIsRunning = false;

	private Location mLocation = null;

	public LocationCollector(Context context) {
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mLocation = getLastKnownLocation(mLocationManager);
	}
	
	public Location getLocation() {
		if (mLocation == null) {
			mLocation = getLastKnownLocation(mLocationManager);
		}
		return mLocation;
	}

	public static Location getLastLocation(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return getLastKnownLocation(lm);
	}

	private static Location getLastKnownLocation(LocationManager locationManager) {
		Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastLocation == null) {
			lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			LogUtil.info("LocationCollector - Get Network Last Known Location: "
					+ StrUtil.strLocation(lastLocation));
		} else {
			LogUtil.info("LocationCollector - Get GPS Last Known Location: "
					+ StrUtil.strLocation(lastLocation));
		}
		return lastLocation;
	}
	
	public void start() {
		if (!mIsRunning) {
			mIsRunning = true;
			startNetworkLocationListener();
			startGPSLocationListener();
		}
	}
	
	public void stop() {
		if (mIsRunning) {
			mIsRunning = false;
			stopGPSLocationListener();
			stopNetworkLocationListener();
		}
	}

	public boolean isRunning() {
		return mIsRunning;
	}

	private void startGPSLocationListener()
	{
		mGPSLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				String str = "LocationCollector - GPS - onLocationChanged - " +
						StrUtil.strLocationAndTime(location);
				LogUtil.info(str);
				mLocation = location;
				stopNetworkLocationListener();
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				LogUtil.info("LocationCollector - GPS - onStatusChanged()");
			}
			@Override
			public void onProviderEnabled(String provider) {
				mLocation = mLocationManager.getLastKnownLocation(provider);
	            LogUtil.info("LocationCollector - GPS - onProviderEnabled()");
			}
			@Override
			public void onProviderDisabled(String provider) {
				LogUtil.info("LocationCollector - GPS - onProviderDisabled()");
			}
		};
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1,
				mGPSLocationListener);
		mIsListenGPSLocation = true;
        LogUtil.info("LocationCollector - GPS - Start Listener");
	}

	private void startNetworkLocationListener() {
		mNetworkLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				String str = "LocationCollector - Network - onLocationChanged - " +
						StrUtil.strLocationAndTime(location);
				LogUtil.info(str);
				mLocation = location;
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				LogUtil.info("LocationCollector - Network - onStatusChanged()");
			}
			@Override
			public void onProviderEnabled(String provider) {
				mLocation = mLocationManager.getLastKnownLocation(provider);
	            LogUtil.info("LocationCollector - Network - onProviderEnabled()");
			}
			@Override
			public void onProviderDisabled(String provider) {
				LogUtil.info("LocationCollector - Network - onProviderDisabled()");
			}
		};
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				2000, 1, mNetworkLocationListener);
		mIsListenNetWorkLocation = true;
        LogUtil.info("LocationCollector - Network - Start Listener");
	}

	private void stopGPSLocationListener() {
		if (mIsListenGPSLocation) {
			mLocationManager.removeUpdates(mGPSLocationListener);			
			LogUtil.info("LocationCollector - GPS - Stop Listener");
		}
	}
	
	private void stopNetworkLocationListener() {
		if (mIsListenNetWorkLocation) {
			mLocationManager.removeUpdates(mNetworkLocationListener);
			LogUtil.info("LocationCollector - Network - Stop Listener");
		}
	}

    public static boolean isLocationEnable(Context context) {
    	if (isGPSLocationOpen(context) || isNetworkLocationOpen(context)) {
    		return true;
    	} else {
            LogUtil.info("LocationCollector - Can't Get Location");
    		return false;
    	}
    }

    public static boolean isGPSLocationOpen(Context context) { 
    	LocationManager locationManager = (LocationManager) context.
				getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps) {
            LogUtil.info("LocationCollector - GPS is open");
            return true;
        } else {
            LogUtil.info("LocationCollector - GPS is close");
	        return false;
        }
    }

    public static boolean isNetworkLocationOpen(Context context) {
    	LocationManager locationManager = (LocationManager) context.
			    getSystemService(Context.LOCATION_SERVICE);
    	boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (network) {
            LogUtil.info("LocationCollector - NetWork Location is open");
            return true;
        } else {
	        LogUtil.info("LocationCollector - NetWork Location is close");
	        return false;
        }
    }

}
