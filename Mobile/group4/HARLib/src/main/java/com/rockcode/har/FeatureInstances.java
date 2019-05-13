package com.rockcode.har;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;


import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * FeatureInstances is wrapper class of weka.core.Instances.
 * This class offer methods for loading ARFF file and trans feature to Instance.
 */
public class FeatureInstances {

	/**
	 * Class for handling an ordered set of weighted instances
	 */
	private Instances mInstances;

	/**
	 * constructor
	 * @param context Context
	 */
	public FeatureInstances(Context context) {
		loadHeadARFF(context);
	}

	/**
	 * load head.arff file, which is contain the struct of data set
	 * @param context Context
	 */
	public void loadHeadARFF(Context context) {
		InputStream is = context.getResources().openRawResource(R.raw.head);
		DataSource datasource = new DataSource(is);	
		try {
			mInstances = datasource.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// set target class index (activity)
		if (mInstances.classIndex() == -1) {
			mInstances.setClassIndex(mInstances.numAttributes() - 1);				
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * save instances to arff file
	 * @param filename filename to save
	 * @throws IOException
	 */
	public void saveToARFF(String filename) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(mInstances);
		File f = new File(filename);
		saver.setFile(f);
		saver.writeBatch();
	}

	public void addInstance(Instance instance) {
		mInstances.add(instance);
	}

	public Instance getLastInstance() {
		return mInstances.lastInstance();
	}

	public Instances getInstances() {
		return mInstances;
	}
	
	public int count() {
		return mInstances.numInstances();
	}
	
	public void clear() {
		mInstances.delete();
	}

	/**
	 * get the index of target class(activity), this is define in ARFF files
	 * @param classValue activity
	 * @return index
	 */
	public int indexOfClassValue(String classValue) {
		return mInstances.classAttribute().indexOfValue(classValue);
	}

	/**
	 *	get the target class(activity) on index, this is define in ARFF files
	 * @param index
	 * @return target class(activity)
	 */
	public String classValueOf(int index) {
		return mInstances.classAttribute().value(index);
	}

	/**
	 * parse Instance from String
	 * @param strLine String Line
	 * @return Instance
	 */
	public DenseInstance parseInstance(String strLine) {
		double[] vals = new double[mInstances.numAttributes()];
		String ss[] = strLine.split(",");
		//set 0 user id
		vals[0] = mInstances.attribute(0).addStringValue(ss[0]);
		//set 1-43 features
		for(int i=0; i < TupleFeature.FEATURES_NUM; i++) {
			vals[i+1] = Double.parseDouble(ss[i+1]);
		}
		//set 44 class
		vals[44] = indexOfClassValue(ss[44]);
		//add the new instance
		DenseInstance instance = new DenseInstance(1.0, vals);
		instance.setDataset(mInstances);
		return instance;
	}

	/**
	 * parse Instance from TupleFeature
	 * @param tupleFeature TupleFeature
	 * @return Instance
	 */
	public DenseInstance parseInstance(TupleFeature tupleFeature) {
		double[] vals = new double[mInstances.numAttributes()];
		//set 0 user id
		vals[0] = mInstances.attribute(0).addStringValue("" + tupleFeature.getUserId());
		//set 1-43 features
		for(int i=0; i < TupleFeature.FEATURES_NUM; i++) {
			vals[i+1] = tupleFeature.getFeatures()[i];
		}
		//set 44 class
		vals[44] = indexOfClassValue(tupleFeature.getActivity());
		//add the new instance
		DenseInstance instance = new DenseInstance(1.0, vals);
		instance.setDataset(mInstances);
		return instance;
	}
}

