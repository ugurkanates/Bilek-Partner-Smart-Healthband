package com.rockcode.har.demo.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.rockcode.har.HumanActivity;


public class HarDataStatic implements Parcelable{
	
	public int mAllRecordNum;
	public int mWalkingRecordNum;
	public int mJoggingRecordNum;
	public int mStandingRecordNum;
	public int mStairsRecordNum;
	public int mSittingRecordNum;
	public int mCyclingRecordNum;

	public HarDataStatic() {
		
	}
	
	public int getActivityRecordNum(String activity) {
		switch (activity) {
		case HumanActivity.ACTIVITY_WALKING:
			return mWalkingRecordNum;
		case HumanActivity.ACTIVITY_JOGGING:
			return mJoggingRecordNum;
		case HumanActivity.ACTIVITY_STANDING:
			return mStandingRecordNum;
		case HumanActivity.ACTIVITY_STAIRS:
			return mStairsRecordNum;
		case HumanActivity.ACTIVITY_SITTING:
			return mSittingRecordNum;
		case HumanActivity.ACTIVITY_CYCLING:
			return mCyclingRecordNum;
		default:
			return 0;
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mAllRecordNum);
		dest.writeInt(mWalkingRecordNum);
		dest.writeInt(mJoggingRecordNum);
		dest.writeInt(mStandingRecordNum);
		dest.writeInt(mStairsRecordNum);
		dest.writeInt(mSittingRecordNum);
		dest.writeInt(mCyclingRecordNum);
	}
	
    public static final Parcelable.Creator<HarDataStatic> CREATOR 
		= new Parcelable.Creator<HarDataStatic>() {

		@Override
		public HarDataStatic createFromParcel(Parcel source) {
			return new HarDataStatic(source);
		}

		@Override
		public HarDataStatic[] newArray(int size) {
			return new HarDataStatic[size];
		}
	
    };

	private HarDataStatic(Parcel in) {
		mAllRecordNum = in.readInt();
		mWalkingRecordNum = in.readInt();
		mJoggingRecordNum = in.readInt();
		mStandingRecordNum = in.readInt();
		mStairsRecordNum = in.readInt();
		mSittingRecordNum = in.readInt();
		mCyclingRecordNum = in.readInt();
	}
}
