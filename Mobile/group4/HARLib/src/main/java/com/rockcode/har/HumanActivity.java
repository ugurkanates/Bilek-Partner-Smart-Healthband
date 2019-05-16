package com.rockcode.har;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import weka.core.DenseInstance;

/**
 * Human Activity
 */
public class HumanActivity implements Serializable, Parcelable {

	private static final long serialVersionUID = 1L;

	// target activity, as same as res/raw/head.arff class define

	public static final String ACTIVITY_NOLABEL = "NoLabel";
	public static final String ACTIVITY_WALKING = "Walking";
	public static final String ACTIVITY_JOGGING = "Jogging";
	public static final String ACTIVITY_CYCLING = "Cycling";
	public static final String ACTIVITY_STAIRS = "Stairs";
	public static final String ACTIVITY_SITTING = "Sitting";
	public static final String ACTIVITY_STANDING = "Standing";

	/**
	 * user id
	 */
	public int mUerid = 0;

	/**
	 * start time
	 */
	public long mStartTime = 0;

	/**
	 * recognition result
	 */
	public String mActivity = ACTIVITY_NOLABEL;

	/**
	 * has been labeled right?
	 */
	public boolean mLabeledRight = false;

	/**
	 * weka instance, represent features
	 */
	private DenseInstance mInstance;

	/**
	 * constructor
	 * @param userid userid, can be any string, default is "0"
	 * @param tupleFeature TupleFeature(has been set classify result)
	 * @param instance Instance(has been set classify result)
	 */
	public HumanActivity(int userid, TupleFeature tupleFeature, DenseInstance instance) {
		mUerid = userid;
		mStartTime = tupleFeature.getStartTime();
		mActivity = tupleFeature.getActivity();
		mInstance = instance;
	}

	/**
	 * constructor
	 * @param tupleFeature TupleFeature(has been set classify result)
	 * @param instance Instance(has been set classify result)
	 */
	public HumanActivity(TupleFeature tupleFeature, DenseInstance instance) {
		mStartTime = tupleFeature.getStartTime();
		mActivity = tupleFeature.getActivity();
		mInstance = instance;
	}

	/**
	 * constructor
	 * @param startTime recognition result start time
	 * @param activity recognition result activity
	 */
	public HumanActivity(long startTime, String activity) {
		mStartTime = startTime;
		mActivity = activity;
	}

	/***
	 * label the activity is right
	 * @param isRight true: right, false: wrong
	 */
	public void labelRight(boolean isRight) {
		if(mLabeledRight != isRight){
			mLabeledRight = isRight;
			LogUtil.info("HarLib - Labeled Right Change: " + mStartTime + " " + mLabeledRight);
		}
	}

	/**
	 * get Instance
	 * @return Instance
	 */
	public DenseInstance getInstance() {
		return mInstance;
	}

	/**
	 * set Instance
	 * @param instance Instance
	 */
	public void setInstance(DenseInstance instance) {
		mInstance = instance;
	}

	@Override
	public String toString() {
		// format:
		//		0		1		2		  3
		//   userid,starttime,activity,isright;\n
		String str = "";
		str += mUerid;
		str += ",";
		str += mStartTime;
		str += ",";
		str += mActivity;
		str += ",";
		str += Boolean.toString(mLabeledRight);
		str += ";\n";
		return str;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mUerid);
		dest.writeLong(mStartTime);
		dest.writeString(mActivity);
		dest.writeValue(mLabeledRight);
		dest.writeSerializable(mInstance);
	}
	
	public static final Parcelable.Creator<HumanActivity> CREATOR 
		= new Parcelable.Creator<HumanActivity>() {

			@Override
			public HumanActivity createFromParcel(Parcel source) {
				return new HumanActivity(source);
			}

			@Override
			public HumanActivity[] newArray(int size) {
				return new HumanActivity[size];
			}
		};

	private HumanActivity(Parcel in) {
		mUerid = in.readInt();
		mStartTime = in.readLong();
		mActivity = in.readString();
		mLabeledRight = (boolean) in.readValue(null);
		mInstance = (DenseInstance) in.readSerializable();
	}
}
