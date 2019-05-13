package com.rockcode.har;

import java.util.List;

import android.content.Context;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * preprocessor the raw sensor data. trans raw data to Instance, which can input to Classifier.
 */
class DataPreprocessor {

	/**
	 * Wrapper of Weka Instances
	 */
	private FeatureInstances mFeatureInstances;

	/**
	 * extract features from raw sensor data
	 */
	private TupleFeature mCurrentFeature;

	/**
	 * pass to Classifier
	 */
	private Instance mCurrentInstance;

	/**
	 * constructor
	 * @param context Context
	 */
	DataPreprocessor(Context context) {
		mFeatureInstances = new FeatureInstances(context);
	}
	
	/**
	 * pre process sensor data, trans raw data to Instance
	 * @param rawDataList raw data list
	 * @return Instance
	 */
	DenseInstance doPreprocess(List<RawData> rawDataList) {
		LogUtil.info("DataPreprocessor - doPreprocess");
		// rawdata ===> feature
		RawData rawdata0 = rawDataList.get(0);
		TupleFeature feature = new TupleFeature(HarConfigs.getUserId(),
				rawdata0.mActivity, rawdata0.mTimeStamp);
		feature.setRawData(rawDataList);
		FeatureExtraction.processTuple(feature);
		mCurrentFeature = feature;
		// tuple feature ===> weka instance
		DenseInstance instance = mFeatureInstances.parseInstance(feature);
		mCurrentInstance = instance;
		return instance;
	}

	/**
	 * clear Instances data
	 */
	void clear() {
		mFeatureInstances.clear();
	}

	/**
	 * get Weka Instances
	 * @return Instances
	 */
	Instances getWekaInstances() {
		return mFeatureInstances.getInstances();
	}

	/**
	 * set classify result to Instance and TupFeature
	 * @param res Classifier Result
	 */
	void setClassifyResult(double res) {
		mCurrentInstance.setClassValue(res);
		mCurrentFeature.setActivity(mFeatureInstances.classValueOf((int)res));
	}

	/**
	 * get current TupFeature
	 * @return TupFeature
	 */
	TupleFeature getCurrentFeature() {
		return mCurrentFeature;
	}
}



