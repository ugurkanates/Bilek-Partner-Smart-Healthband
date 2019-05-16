package com.rockcode.har;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Collect the accelerometer sensor data for a period of time(one recognition cycle).
 */
class SensorDataCollector {

	/**
	 * use to label target activity on collect mode
	 */
	private String mActivity = HumanActivity.ACTIVITY_NOLABEL;

	/**
	 * the collect sensor data list on this cycle
	 */
	private List<RawData> mRawDataList;

	/**
	 * sensor data collect listener
	 */
	private SensorDataListener mSensorDataListener;

	private SensorManager mSensorManager = null;
	private Sensor mAcceleroMeterSensor = null;

	/**
	 *	SensorDataCollector
	 * @param context Context
	 * @param sensorDataListener Sensor data Collect Listener
	 */
	SensorDataCollector(Context context, SensorDataListener sensorDataListener) {
		mSensorDataListener = sensorDataListener;
		mRawDataList = new ArrayList<>();
		// get accelerometer Sensor Service
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mAcceleroMeterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	/**
	 * set label target activity on collect mode
	 * @param activity need label activity
	 */
	void setLabelActivity(String activity) {
		mActivity = activity;
	}

	/**
	 * start collect
	 * @return true:success, false:failure
	 */
	boolean start() {
		if(mSensorManager != null && mAcceleroMeterSensor != null) {
			int samplingPeriodUs = (1000000 / HarConfigs.getSampleFreq());
			mSensorManager.registerListener(mSensorEventListener, mAcceleroMeterSensor, samplingPeriodUs);
			LogUtil.info("HarLib - Start Collect Sensor Data");
			return true;
		} else {
			LogUtil.info("HarLib - Start Collect Sensor Data Failure !");
			return false;
		}
	}

	/**
	 * stop collect
	 * @return true:success, false:failure
	 */
	boolean stop() {
		if(mSensorManager != null && mAcceleroMeterSensor != null) {
			mSensorManager.unregisterListener(mSensorEventListener, mAcceleroMeterSensor);
			LogUtil.info("HarLib - Stop Collect Sensor Data");
			return true;
		} else {
			LogUtil.info("HarLib - Stop Collect Sensor Data Failure !");
			return false;
		}
	}

	private SensorEventListener mSensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			// add to RawDataList
			RawData rawData = addRawData(event.timestamp, event.values[0], event.values[1], event.values[2]);
			// get diff time of this collect cycle
			long alreadyCollectTime = mRawDataList.get(0).diffTimeAbs(rawData);
			// has collect enought time ?
			if( ( alreadyCollectTime > HarConfigs.getSensorCollectTime()) ){
				// has collect enough data number ?
				if(mRawDataList.size() >= HarConfigs.getLeastSampleNumber()) {
					LogUtil.info("HarLib - " + "collect time: " + alreadyCollectTime
							+ " RawData Num: " + mRawDataList.size());
					// notify data collect ok
					mSensorDataListener.onSensorDataChange(mRawDataList);
					mRawDataList.clear();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	/**
	 * add RawData to List
	 * @param timestamp Sensor event timestamp
	 * @param x	The X axis value
	 * @param y	The Y axis value
	 * @param z The Z axis value
	 * @return RawData
	 */
	private RawData addRawData(long timestamp, float x, float y, float z) {
		RawData rawData = new RawData(mActivity, timestamp, x, y, z);
		mRawDataList.add(rawData);
		return rawData;
	}

}
