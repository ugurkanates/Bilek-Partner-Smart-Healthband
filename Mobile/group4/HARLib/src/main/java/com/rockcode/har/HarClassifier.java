package com.rockcode.har;

import java.io.InputStream;

import android.content.Context;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Human Activity Recognition Classifier
 */
class HarClassifier {

	/**
	 * Weka Classifier with filter
	 */
	private FilteredClassifier mClassifier = null;

	/**
	 * Weka Instance, which is the features struct define
	 */
	private Instances mInstances;

	private Context mContext;

	/**
	 * constructor
	 * @param context Context
	 * @param instances Weka Instances
	 */
	HarClassifier(Context context, Instances instances){
		mContext = context;
		mInstances = instances;
		setDefaultClassifier();
	}

	/**
	 * constructor
	 * @param context Context
	 * @param instances Weka Instalces
	 * @param classifierFilePath Classifier model filepath
	 */
	HarClassifier(Context context, Instances instances, String classifierFilePath) {
		mContext = context;
		mInstances = instances;
		setClassifier(classifierFilePath);
	}

	/**
	 * classify the Instance
	 * @param instance Weka Instance
	 * @return double result
	 */
	double classifyData(Instance instance) {
		double res = 0;
		try {
			res = mClassifier.classifyInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.info("HarLib - Classify Result: "
				+ mInstances.classAttribute().value((int) res));
		//		instance.setClassValue(res);
		return res;
	}

	/**
	 * set classifier filter, remove useless feature column in Instance
	 * @param fc FilteredClassifier need to set
	 */
	private void setFilter(FilteredClassifier fc) {
		Remove rm = new Remove();
		String[] opt = new String[2];
		opt[0] = "-R";
		opt[1] = "1";
		try {
			rm.setInputFormat(mInstances);
			rm.setOptions(opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fc.setFilter(rm);
	}

	/**
	 * set use default classifier file in res/raw/j48_new.model
	 * @return true: success, false: failure
	 */
	private boolean setDefaultClassifier() {
		return setClassifier(loadClassifier(R.raw.j48_new));
	}

	/**
	 * set use the classifier of given filepath
	 * @param filepath classifier model filepath
	 * @return true: success, false: failure
	 */
	boolean setClassifier(String filepath) {
		return setClassifier(loadClassifier(filepath));
	}

	/**
	 * set use the given classifier
	 * @param cls Classifier instance
	 * @return
	 */
	boolean setClassifier(Classifier cls) {
		mClassifier = new FilteredClassifier();
		mClassifier.setClassifier(cls);
		setFilter(mClassifier);
		return mClassifier != null;
	}

	/**
	 * load Classifier with given filepath
	 * @param filepath Classifier model filepath
	 * @return Classifier instance
	 */
	private Classifier loadClassifier(String filepath) {
		Classifier cls = null;
		try {
			cls = (Classifier) SerializationHelper.read(filepath);
			LogUtil.info("HarLib - HarClassifier - Load Classifier Success: " + filepath);
		} catch (Exception e) {
			LogUtil.err("HarLib - HarClassifier - Load Classifier  Fails");
			e.printStackTrace();
			return null;
		}
		return cls;
	}

	/**
	 * load the classifier with given resource id
	 * @param rawid resource id
	 * @return Classifier instance
	 */
	private Classifier loadClassifier(int rawid) {
		Classifier cls = null;
		InputStream is = mContext.getResources().openRawResource(rawid);
		try {
			cls = (Classifier) SerializationHelper.read(is);
			LogUtil.info("HarLib - HarClassifier - Load Classifier Success");
		} catch (Exception e) {
			LogUtil.err("HarLib - HarClassifier - Load Classifier Fails");
			e.printStackTrace();
			return null;
		}
		return cls;
	}

}
