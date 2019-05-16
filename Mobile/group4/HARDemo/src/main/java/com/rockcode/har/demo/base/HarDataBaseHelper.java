package com.rockcode.har.demo.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rockcode.har.HumanActivity;
import com.rockcode.har.demo.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class HarDataBaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "har_db";

	private static final String TABLE_HAR = "har";
	private static final String COLUMN_STARTTIME = "start_time";
	private static final String COLUMN_ACTIVITY = "activity";
	private static final String COLUMN_GPSTIME = "gpstime";
	private static final String COLUMN_LATITUDE = "latitude";
	private static final String COLUMN_LONGITUDE ="longitude";

	private static final int VERSION = 1;

	public HarDataBaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL( "create table " + TABLE_HAR + " ( " +
						COLUMN_STARTTIME + " integer, " +
						COLUMN_ACTIVITY + " varchar(20), "+
						COLUMN_GPSTIME + " integer, " +
						COLUMN_LATITUDE + " real," +
						COLUMN_LONGITUDE + " real" +
						" )" );
			LogUtil.info("HarDataBaseHelper - creat database table har ok!");
		} catch (SQLException e) {
			LogUtil.err("HarDataBaseHelper - " + e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public void insertHarList(List<UserActivityInfo> harList) {
		for (UserActivityInfo ha : harList) {
			insertHar(ha);
		}
	}

	private long insertHar(UserActivityInfo ha) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_STARTTIME, ha.mStartTime);
		cv.put(COLUMN_ACTIVITY, ha.mActivity);
		cv.put(COLUMN_GPSTIME, ha.mGpsTime);
		cv.put(COLUMN_LATITUDE, ha.mLatitude);
		cv.put(COLUMN_LONGITUDE, ha.mLongitude);
		return getWritableDatabase().insert(TABLE_HAR, null, cv);
	}

	public ArrayList<UserActivityInfo> queryHarList(long startTime, long finishTime) {
		ArrayList<UserActivityInfo> harList = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_HAR, null,
				COLUMN_STARTTIME + ">=?" + " and " + COLUMN_STARTTIME + "<=?",
				new String[]{startTime+"", finishTime+""},
				null, null, COLUMN_STARTTIME + " asc");
		while (cursor.moveToNext()) {
			harList.add( getActivityFromCursor(cursor) );
		}
		return harList;
	}

	public UserActivityInfo queryHarByStartTime(long startTime) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_HAR, null,
				COLUMN_STARTTIME + "=?", new String[]{startTime+""},
				null, null, COLUMN_STARTTIME+" asc");
		cursor.moveToFirst();
		UserActivityInfo ha = getActivityFromCursor(cursor);
		if(cursor.getCount() > 1) {
			LogUtil.info("HarDataBaseHelper - duplicate data in database, start_time: " + ha.mStartTime);
		}
		return ha;
	}

	public int getAllRecordNum() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = db.query(TABLE_HAR, null, null, null, null, null, null);
			if(cursor != null) {
				count = cursor.getCount();				
			}
		} finally {
			cursor.close();
		}
		return count;
	}

	public int getActivityRecordNum(String activity) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = db.query(TABLE_HAR, null,
					COLUMN_ACTIVITY + "=?", new String[]{activity},
					null, null, null);
			if(cursor!=null) {
				count = cursor.getCount();				
			}
		} finally {
			cursor.close();
		}
		return count;
	}

	private UserActivityInfo getActivityFromCursor(Cursor cursor) {
		long startTime = cursor.getLong(cursor.getColumnIndex(COLUMN_STARTTIME));
		String activity = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY));
		long gpsTime = cursor.getLong(cursor.getColumnIndex(COLUMN_GPSTIME));
		double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
		double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
		UserActivityInfo info = new UserActivityInfo(startTime, activity, gpsTime, latitude, longitude);
		return info;
	}

	public HarDataStatic getHarDataStatic() {
		HarDataStatic harDataStatic = new HarDataStatic();
		harDataStatic.mAllRecordNum = getAllRecordNum();
		harDataStatic.mWalkingRecordNum = getActivityRecordNum(HumanActivity.ACTIVITY_WALKING);
		harDataStatic.mJoggingRecordNum = getActivityRecordNum(HumanActivity.ACTIVITY_JOGGING);
		harDataStatic.mCyclingRecordNum = getActivityRecordNum(HumanActivity.ACTIVITY_CYCLING);
		harDataStatic.mStairsRecordNum = getActivityRecordNum(HumanActivity.ACTIVITY_STAIRS);
		harDataStatic.mStandingRecordNum = getActivityRecordNum(HumanActivity.ACTIVITY_STANDING);
		harDataStatic.mSittingRecordNum = getActivityRecordNum(HumanActivity.ACTIVITY_SITTING);
		return harDataStatic;
	}
}
