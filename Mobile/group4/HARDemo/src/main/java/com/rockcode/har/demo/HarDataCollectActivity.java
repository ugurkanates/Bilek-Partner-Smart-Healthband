package com.rockcode.har.demo;

import com.rockcode.har.HarDataListener;
import com.rockcode.har.HarMode;
import com.rockcode.har.HumanActivity;
import com.rockcode.har.HumanActivityRecognizer;
import com.rockcode.har.RawData;
import com.rockcode.har.demo.util.LogUtil;
import com.rockcode.har.demo.util.StrUtil;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HarDataCollectActivity extends ActionBarActivity {

	private HumanActivityRecognizer mHumanActivityRecognizer;
	private boolean mIsRunning = false;
	
	private String mLabelActivity = HumanActivity.ACTIVITY_NOLABEL;
	private int mCollectCount = 0;
	private long mStartTime = 0;
	private long mFinishTime = 0;
	private String mSaveFileName = null;
	
	private TextView mTextViewLabelActivity;
	private TextView mTextViewCollectNum;
	private TextView mTextViewStartTime;
	private TextView mTextViewFinishTime;

	private List<RawData> mRawDataList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har_data_collect);
		initDisplayInfo();
		initHAR();
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsRunning) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private void initHAR() {
		mHumanActivityRecognizer = new HumanActivityRecognizer(this, true, HarMode.COLLECT,
				mHarDataListener);
	}

	private HarDataListener mHarDataListener = new HarDataListener() {
		@Override
		public void onHarDataChange(HumanActivity ha) {
		}

		@Override
		public void onHarRawDataChange(List<RawData> rawDataList) {
			mCollectCount += rawDataList.size();
			mTextViewCollectNum.setText(mCollectCount + "");
			Toast.makeText(getApplicationContext(), "Collect Sensor Data: " + rawDataList.size(),
					Toast.LENGTH_SHORT).show();
			// save data
			mRawDataList.addAll(rawDataList);
			if (mRawDataList.size() > 1200) {
				saveRawData2File();
			}
		}
	};

	private void saveRawData2File() {
		saveRawData2File(mSaveFileName, mRawDataList);
		mRawDataList.clear();
	}

	private boolean saveRawData2File(String filename, List<RawData> rawdataList) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			LogUtil.err("external storage unavailable!");
			return false;
		}
		// crate dir if not exist
		File saveFileDir = new File(Environment.getExternalStorageDirectory() + "/raw/");
		if (!saveFileDir.exists()) {
			saveFileDir.mkdirs();
		}
		// append to file
		File saveFilePath = new File(saveFileDir, filename);
		FileWriter fw = null;
		try {
			fw = new FileWriter(saveFilePath, true);
			for (RawData data : rawdataList) {
				fw.append(data.toString());
			}
			fw.flush();
			LogUtil.info("HarDataStorage - Save RawData: " + rawdataList.size()
					+ " to file:" + saveFilePath.getAbsolutePath());
		} catch (IOException e) {
			LogUtil.err("HarDataStorage - " + e.toString());
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				LogUtil.err("HarDataStorage - " + e.toString());
				e.printStackTrace();
			}
		}
		return true;
	}

	private void initDisplayInfo() {
		mTextViewLabelActivity = (TextView) findViewById(R.id.TextView_labelactivity);
		mTextViewCollectNum = (TextView) findViewById(R.id.textView_collect_num);
		mTextViewStartTime = (TextView) findViewById(R.id.textView_starttime);
		mTextViewFinishTime = (TextView) findViewById(R.id.textView_finish_time);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.har_data_collect, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
        case R.id.action_classify_mode:
	        if (mIsRunning) {
		        Toast.makeText(getApplicationContext(), getString(R.string.hint_stop_har_first),
				        Toast.LENGTH_SHORT).show();
	        } else {
		        Intent intent = new Intent();
		        intent.setClass(this, MainActivity.class);
		        startActivity(intent);
		        finish();
	        }
            return true;
            
        case R.id.action_start:
			if(mIsRunning) {
				mIsRunning = false;
				// stop har
				mHumanActivityRecognizer.stop();
				// save to file
				saveRawData2File();
				mSaveFileName = null;
				// update ui
				mFinishTime = System.currentTimeMillis();
				item.setIcon(getResources().getDrawable(R.drawable.play_dark));
				mTextViewFinishTime.setText(StrUtil.strTime(mFinishTime, "yyyy-MM-dd HH:mm:ss"));
				Toast.makeText(this, getString(R.string.hint_har_stop), Toast.LENGTH_SHORT).show();
			} else {
				mIsRunning = true;
				mCollectCount = 0;
				mStartTime = System.currentTimeMillis();
				// set save filename
				String mStartTimeStr = StrUtil.strTime(mFinishTime, "yyyyMMdd_HHmmss");
				mSaveFileName = "rawdata_" + mStartTimeStr + ".txt";
				// start har
				mHumanActivityRecognizer.start();
				// update ui
				item.setIcon(getResources().getDrawable(R.drawable.pause));
				mTextViewCollectNum.setText(mCollectCount+"");
				mTextViewStartTime.setText(StrUtil.strTime(mStartTime, "yyyy-MM-dd HH:mm:ss"));
				Toast.makeText(this, getString(R.string.hint_har_start), Toast.LENGTH_SHORT).show();
			}
            return true;
            
        case R.id.action_select_activity:
        	selectTargetActivity(item);
        	return true;
        	
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void selectTargetActivity(MenuItem item) {
    	new AlertDialog.Builder(this)
    	.setTitle("Label Activity")
		.setSingleChoiceItems(
    		new String[] { HumanActivity.ACTIVITY_NOLABEL, 
    			HumanActivity.ACTIVITY_WALKING, 
    			HumanActivity.ACTIVITY_JOGGING,
    			HumanActivity.ACTIVITY_STAIRS, 
    			HumanActivity.ACTIVITY_STANDING,
    			HumanActivity.ACTIVITY_SITTING }, 0,
    		new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			switch(which) {
        			    case 0:
				            mLabelActivity = HumanActivity.ACTIVITY_NOLABEL;
				            break;
        			    case 1:
				            mLabelActivity = HumanActivity.ACTIVITY_WALKING;
				            break;
        			    case 2:
				            mLabelActivity = HumanActivity.ACTIVITY_JOGGING;
				            break;
        			    case 3:
				            mLabelActivity = HumanActivity.ACTIVITY_STAIRS;
				            break;
        			    case 4:
				            mLabelActivity = HumanActivity.ACTIVITY_STANDING;
				            break;
        			    case 5:
				            mLabelActivity = HumanActivity.ACTIVITY_SITTING;
				            break;
        			}
        			mHumanActivityRecognizer.setLabelActivity(mLabelActivity);
        			mTextViewLabelActivity.setText(mLabelActivity);
        			Toast.makeText(getApplicationContext(), "Set Label Activity:" + mLabelActivity,
					        Toast.LENGTH_SHORT).show();
        			dialog.dismiss();
        		}
		    }
		)
    	.setNegativeButton("CANCEL", null)
    	.show();
	}
}
