package com.rockcode.har;

import java.util.List;

import weka.core.DenseInstance;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * This is HAR (Human Activity Recognizer) library main class. You can simply create instance of
 * this class and call start() method, then will get recognition result in listener.
 *
 * 1.The Process of HAR
 * The process of HAR as below diagram:
 *
 *           .------------.        .--------------.        .------------.
 *  Sensor   |            |        |              |        |            |Activity
 *  Event    | SensorData |RawData | Data         |Instance| Classifier |(walking, sitting...etc)
 *  -------> | Collector  |------->| Preprocessor |------->|            |------->
 *           |            |        |              |        |            |
 *           '------------'        '--------------'        '------------'
 *
 * Just as there has three main process, the HAR has three corresponding class:
 *
 * 1) SensorDataCollector
 *  Collect a period of time of accelerometer sensor data on android phone. Then input an array
 *  of RawData into DataPreprocessor.
 *
 * 2) DataPreprocessor
 *  This class will extract features from the array of RawData, and then trans these feature into
 *  Weka's class - Instance, which can pass to Classifier.
 *
 * 3) Classifier
 *  Classifier will process the Instance, then give an classify result. The Classifier will import
 *  the mode file on initial phase, which was trained and created on Weka desktop version.
 *  The default Classifier use Decision Tree J48 algorithm, it has pretty good performance. If you
 *  need to try another algorithm, you can pass the path of your classifier model file as parameter
 *  into the constructor of HumanActivityRecognizer.
 *
 * 2.Running Mode
 * HAR has two running mode: CLASSIFY and COLLECT. when running in COLLECT mode, there is only
 * SensorDataCollector running.
 *
 */
public class HumanActivityRecognizer {

	/**
	 * HAR data listener
	 */
	private HarDataListener mHarDataListener;

	/**
	 * use to collect sensor data
	 */
	private SensorDataCollector mSensorDataCollector;

	/**
	 * pre process the raw sensor data before input to classifier
	 */
	private DataPreprocessor mDataPreprocessor;

	/**
	 * HAR classifier
	 */
	private HarClassifier mHarClassifier;

	/**
	 * HAR running state
	 */
	private boolean mIsRunning = false;

	/**
	 * HAR running mode, current has two mode: CLASSIFY and COLLECT
	 */
	private HarMode mRunningMode = HarMode.CLASSIFY;

	private Context mContext;
	private WakeLock mWakeLock;
	private boolean mIsKeepWake = true;
	private static final String HAR_PERSIST = "HAR";

	/**
	 * constructor
	 * @param context Context
	 * @param isKeepWake would HarLib hold the wake lock
	 * @param runningMode har running mode, current has two mode: CLASSIFY and COLLECT
	 * @param harDataListener har data listener
	 */
	public HumanActivityRecognizer(Context context, boolean isKeepWake, HarMode runningMode,
			HarDataListener harDataListener) {
		mContext = context;
		mIsKeepWake = isKeepWake;
		mHarDataListener = harDataListener;
		setRunningMode(runningMode);
		init(null);
	}

	/**
	 * constructor
	 * @param context Context
	 * @param isKeepWake would HarLib hold the wake lock
	 * @param runningMode har running mode, current has two mode: CLASSIFY and COLLECT
	 * @param classifierFilePath Classifier model filepath
	 * @param harDataListener har data listener
	 */
	public HumanActivityRecognizer(Context context, boolean isKeepWake, HarMode runningMode,
	                               String classifierFilePath, HarDataListener harDataListener) {
		mContext = context;
		mIsKeepWake = isKeepWake;
		mHarDataListener = harDataListener;
		setRunningMode(runningMode);
		init(classifierFilePath);
	}

	/**
	 * initialize
	 */
	private void init(String classifierFilePath) {
		mSensorDataCollector = new SensorDataCollector(mContext, mSensorDataListener);
		mDataPreprocessor = new DataPreprocessor(mContext);
		if (classifierFilePath != null) {
			mHarClassifier = new HarClassifier(mContext, mDataPreprocessor.getWekaInstances(),
					classifierFilePath);
		} else {
			mHarClassifier = new HarClassifier(mContext, mDataPreprocessor.getWekaInstances());
		}
		if(mIsKeepWake) {
			PowerManager manager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
			mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, HAR_PERSIST);
		}
	}
	
	/**
	 * start the HAR
	 */
	public void start() {
		mSensorDataCollector.start();
		if(mIsKeepWake && mWakeLock!=null) {
			mWakeLock.acquire();
		}
		mIsRunning = true;
		LogUtil.info("HarLib - Start HAR");
	}
	
	/**
	 * stop the HAR
	 */
	public void stop() {
		mSensorDataCollector.stop();
		if(mIsKeepWake && mWakeLock!=null) {
			mWakeLock.release();
		}
		mIsRunning = false;
		clear();
		LogUtil.info("HarLib - Stop HAR");
	}
	
	/**
	 * return the running state
	 * @return true: running, false: stop
	 */
	public boolean isRunning() {
		return mIsRunning;
	}

	/**
	 * use to label the raw data on COLLECT running mode
	 * @param activity Activity Value(String): NoLabel, Walking, Jogging, Cycling, Stairs, Standing
	 */
	public void setLabelActivity(String activity) {
		mSensorDataCollector.setLabelActivity(activity);
	}

	/**
	 * listen to the SensorDataCollector
	 */
	private SensorDataListener mSensorDataListener = new SensorDataListener() {
		@Override
		public void onSensorDataChange(List<RawData> rawDataList) {
			// notify listener raw sensor data ready
			if (mHarDataListener != null) {
				mHarDataListener.onHarRawDataChange(rawDataList);
			}
			if (mRunningMode == HarMode.CLASSIFY) {
				LogUtil.info("HarLib - CLASSIFY_MODE");
				// pre process data
				DenseInstance instance = mDataPreprocessor.doPreprocess(rawDataList);
				// classify data
				Double res = mHarClassifier.classifyData(instance);
				// set classify result back to Instance and TupleFeature
				instance.setClassValue(res);
				mDataPreprocessor.setClassifyResult(res);
				// create HumanActivity
				TupleFeature curFeature = mDataPreprocessor.getCurrentFeature();
				HumanActivity ha = new HumanActivity(curFeature, instance);
				// notify listener recognition result ready
				if (mHarDataListener != null) {
					mHarDataListener.onHarDataChange(ha);
				}
			}
		}
	};
	
	/**
	 * clear useless data
	 */
	public void clear() {
		LogUtil.info("HarLib - clear");
		mDataPreprocessor.clear();
	}

	/**
	 * set HAR Classifier
	 * @param filepath Classifier filepath
	 * */
	public void setClassifier(String filepath) {
		boolean isHarRunning = mIsRunning;
		if(isHarRunning) {
			stop();
		}
		mHarClassifier.setClassifier(filepath);
		if(isHarRunning) {
			start();
		}
	}
	
	/**
	 * Set HAR running mode
	 * @param mode HarMode.CLASSIFY or HarMode.COLLECT
	 */
	public void setRunningMode(HarMode mode) {
		LogUtil.info("HarLib - HAR Current Mode: "+ mode);
		mRunningMode = mode;
	}
	
}

