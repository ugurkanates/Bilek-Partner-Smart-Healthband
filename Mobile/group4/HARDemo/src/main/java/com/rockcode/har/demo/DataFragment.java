package com.rockcode.har.demo;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.rockcode.har.demo.base.UserActivityInfo;
import com.rockcode.har.demo.util.LogUtil;
import com.rockcode.har.demo.base.HarDataStatic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataFragment extends Fragment {
	
	private GraphicalView mGraphicalView;
	private DefaultRenderer mRenderer;
	private CategorySeries mSeries;
	private TextView mTextViewAllDataNum;
	private TextView mTextViewActivityInfo;
	private TextView mTextViewActivityDataNum;

	SimpleSeriesRenderer mSSRendererWalking = new SimpleSeriesRenderer();
	SimpleSeriesRenderer mSSRendererJogging = new SimpleSeriesRenderer();
	SimpleSeriesRenderer mSSRendererStanding = new SimpleSeriesRenderer();
	SimpleSeriesRenderer mSSRendererStairs = new SimpleSeriesRenderer();
	SimpleSeriesRenderer mSSRendererSitting = new SimpleSeriesRenderer();
	SimpleSeriesRenderer mSSRendererCycling = new SimpleSeriesRenderer();
	
	int mIndexWalking;
	int mIndexJogging;
	int mIndexStanding;
	int mIndexStairs;
	int mIndexSitting;
	int mIndexCycling;
	
	private HarDataStatic mHarDataStatic = null;
	
	public DataFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_data, container, false);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateDisplay(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
		HarService.getHarDataStatic(getActivity().getApplicationContext());
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	private void updateDisplay(HarDataStatic harDataStatic) {
		mHarDataStatic = harDataStatic;
		updateChart(mHarDataStatic);
		updateOverallData(mHarDataStatic);
	}

	private void updateChart(final HarDataStatic harDataStatic) {
		initSeriesAndRenderer("Activity Info" , harDataStatic);
		mGraphicalView = ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);
		mGraphicalView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	            SeriesSelection ss = mGraphicalView.getCurrentSeriesAndPoint();
	            if (ss != null) {
	            	for (int i = 0; i < mSeries.getItemCount(); i++) {
	            		mRenderer.getSeriesRendererAt(i).setHighlighted(i == ss.getPointIndex());
	            	}
	            	mGraphicalView.repaint();
	    			updateActivityInfo(ss.getPointIndex(), harDataStatic);
	    			LogUtil.info("Chart data point index " + ss.getPointIndex()
						    + " selected point value=" + ss.getValue());
	            }
			}
		});
		LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.LinearLayout_chart);
		layout.removeAllViews();
		layout.addView(mGraphicalView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}
	
	private void initSeriesAndRenderer(String title, HarDataStatic harDataStatic) {
		int walkingNum = 0;
		int joggingNum = 0;
		int standingNum = 0;
		int stairsNum = 0;
		int sittingNum = 0;
		int cyclingNum = 0;
		
		if(mHarDataStatic != null) {
			walkingNum = harDataStatic.mWalkingRecordNum;
			joggingNum = harDataStatic.mJoggingRecordNum;
			standingNum = harDataStatic.mStandingRecordNum;
			stairsNum = harDataStatic.mStairsRecordNum;
			sittingNum = harDataStatic.mSittingRecordNum;
			cyclingNum = harDataStatic.mCyclingRecordNum;
		}
		
		mSeries = new CategorySeries(title);
        mRenderer = new DefaultRenderer();
        
        mRenderer.setLegendTextSize(20);
        mRenderer.setZoomEnabled(false);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setPanEnabled(false);
        mRenderer.setClickEnabled(true);
        
        mSSRendererWalking.setColor(Color.GREEN);
        mSSRendererJogging.setColor(Color.CYAN);
        mSSRendererStanding.setColor(Color.BLUE);
        mSSRendererStairs.setColor(Color.YELLOW);
        mSSRendererSitting.setColor(Color.RED);
        mSSRendererCycling.setColor(Color.rgb(241, 141, 0));
        int index = -1;
        mIndexWalking = -1;
        mIndexJogging = -1;
        mIndexStanding = -1;
        mIndexStairs = -1;
        mIndexSitting = -1;
        mIndexCycling = -1;
		if(walkingNum==0 && joggingNum==0 && standingNum==0 && stairsNum==0 && sittingNum==0) {
			// init all num is 0 display
			mSeries.add(UserActivityInfo.ACTIVITY_WALKING, 1);
	        mRenderer.addSeriesRenderer(mSSRendererWalking);
	        mIndexWalking = 0;
			mSeries.add(UserActivityInfo.ACTIVITY_JOGGING, 1);
	        mRenderer.addSeriesRenderer(mSSRendererJogging);
	        mIndexJogging = 1;
			mSeries.add(UserActivityInfo.ACTIVITY_STANDING, 1);
	        mRenderer.addSeriesRenderer(mSSRendererStanding);
	        mIndexStanding = 2;
			mSeries.add(UserActivityInfo.ACTIVITY_STAIRS, 1);
	        mRenderer.addSeriesRenderer(mSSRendererStairs);
	        mIndexStairs = 3;
			mSeries.add(UserActivityInfo.ACTIVITY_SITTING, 1);
	        mRenderer.addSeriesRenderer(mSSRendererSitting);
	        mIndexSitting = 4;
			mSeries.add(UserActivityInfo.ACTIVITY_CYCLING, 1);
	        mRenderer.addSeriesRenderer(mSSRendererCycling);
	        mIndexSitting = 5;
		} else {
			if(walkingNum != 0){
				mSeries.add(UserActivityInfo.ACTIVITY_WALKING, walkingNum);
		        mRenderer.addSeriesRenderer(mSSRendererWalking);
		        index++;
			}
			mIndexWalking = index;
			if(joggingNum != 0) {
				mSeries.add(UserActivityInfo.ACTIVITY_JOGGING, joggingNum);
		        mRenderer.addSeriesRenderer(mSSRendererJogging);
		        index++;
			}
			mIndexJogging = index;
			if(standingNum != 0) {
				mSeries.add(UserActivityInfo.ACTIVITY_STANDING, standingNum);
		        mRenderer.addSeriesRenderer(mSSRendererStanding);
		        index++;
			}
			mIndexStanding = index;
			if(stairsNum != 0) {
				mSeries.add(UserActivityInfo.ACTIVITY_STAIRS, stairsNum);
		        mRenderer.addSeriesRenderer(mSSRendererStairs);
		        index++;
			}
			mIndexStairs = index;
			if(sittingNum != 0) {
				mSeries.add(UserActivityInfo.ACTIVITY_SITTING, sittingNum);
		        mRenderer.addSeriesRenderer(mSSRendererSitting);
		        index++;
			}
			mIndexSitting = index;
			if(cyclingNum != 0) {
				mSeries.add(UserActivityInfo.ACTIVITY_CYCLING, cyclingNum);
		        mRenderer.addSeriesRenderer(mSSRendererCycling);
		        index++;
			}
			mIndexCycling = index;
		}
    }
	
	private void updateOverallData(HarDataStatic harDataStatic) {
		if(harDataStatic == null) {
			return;
		}
		if(mTextViewAllDataNum == null) {
			mTextViewAllDataNum = (TextView) getActivity().findViewById(R.id.textView_all_data_num);
		}
		String strAllDataNum = getActivity().getResources()
				.getString(R.string.textview_all_data_num);
		strAllDataNum += " " + harDataStatic.mAllRecordNum;
		mTextViewAllDataNum.setText(strAllDataNum);
	}
	
	private void updateActivityInfo(int index, HarDataStatic harDataStatic) {
		String activity = UserActivityInfo.ACTIVITY_NOLABEL;
		if(index == mIndexWalking) {
			activity = UserActivityInfo.ACTIVITY_WALKING;
		} else if(index == mIndexJogging) {
			activity = UserActivityInfo.ACTIVITY_JOGGING;
		} else if(index == mIndexStanding) {
			activity = UserActivityInfo.ACTIVITY_STANDING;
		} else if(index == mIndexStairs) {
			activity = UserActivityInfo.ACTIVITY_STAIRS;
		} else if(index == mIndexSitting) {
			activity = UserActivityInfo.ACTIVITY_SITTING;
		} else if(index == mIndexCycling) {
			activity = UserActivityInfo.ACTIVITY_CYCLING;
		}
		updateActivityInfo(activity, harDataStatic);
	}
	
	private void updateActivityInfo(String activity, HarDataStatic harDataStatic) {
		if(mTextViewActivityInfo == null) {
			mTextViewActivityInfo = (TextView) getActivity().findViewById(R.id.textView_activity_info);
		}
		if(mTextViewActivityDataNum == null) {
			mTextViewActivityDataNum = (TextView) getActivity().findViewById(R.id.TextView_activity_data_num);
		}
		//show current select activity
		String strActivityInfo = getActivity().getResources()
				.getString(R.string.textview_activity_info);
		strActivityInfo += " - " + activity;
		mTextViewActivityInfo.setText(strActivityInfo);
		//show activity record num
		if(harDataStatic != null) {
			String strActivityDataNum = getActivity().getResources()
					.getString(R.string.textview_activity_data_num);
			strActivityDataNum += " " + harDataStatic.getActivityRecordNum(activity);
			mTextViewActivityDataNum.setText(strActivityDataNum);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleUpdateHarDataStatic(HarService.HarDataStaticUpdateEvent event) {
		LogUtil.info("DataFragment - handleUpdateHarDataStatic()");
		if (event != null) {
			updateDisplay(event.harDataStatic);
		}
	}
}
