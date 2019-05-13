package com.rockcode.har.demo;

import java.util.Calendar;

import com.rockcode.har.demo.base.LocationCollector;
import com.rockcode.har.demo.util.LogUtil;
import com.rockcode.har.demo.util.StrUtil;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	
	private DataFragment mDataFragment;
	private MapFragment mMapFragment;
	
	private Button mButtonStarTime;
	private Button mButtonFinishTime;
	private ImageButton mButtonControl;
	private boolean mIsHarRunning = false;
	private boolean mIsShowHistoryData = false;
	private long mSelectStartTime = 0;
	private long mSelectFinishTime = 0;

	private MenuItem mStartMenuItem;
	private MenuItem mShowHistoryMenuItem;

	View mViewButtons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		HarService.startService(getApplicationContext());
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if(!mIsHarRunning){
			HarService.stopService(getApplicationContext());	
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mStartMenuItem = menu.findItem(R.id.action_start);
		mShowHistoryMenuItem = menu.findItem(R.id.action_show_history_data);
	    return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {

	        case R.id.action_start:
				if(mIsHarRunning) {
					mIsHarRunning = false;
					Toast.makeText(getApplicationContext(), getString(R.string.hint_har_stop),
							Toast.LENGTH_SHORT).show();
					HarService.stopHar(getApplicationContext());
				} else {
					if(!LocationCollector.isGPSLocationOpen(getApplicationContext())) {
						showAlertDialogToOpenGPS();
					} else {
						mIsHarRunning = true;
						mMapFragment.clearOverlay();
						Toast.makeText(getApplicationContext(),getString(R.string.hint_har_start),
								Toast.LENGTH_SHORT).show();
						HarService.startHar(getApplicationContext());
					}
				}
				setHarStatusView(mIsHarRunning);
	            return true;

	        case R.id.action_show_history_data:
	            setShowHistoryStatusView();
	            return true;

		    case R.id.action_collect_mode:
			    HarService.stopService(getApplicationContext());
			    Intent intent = new Intent();
			    intent.setClass(this, HarDataCollectActivity.class);
			    startActivity(intent);
			    finish();
			    return true;

	        case R.id.action_clear:
	            mMapFragment.clearOverlay();
	            mButtonStarTime.setText(R.string.button_start_time);
	            mButtonFinishTime.setText(R.string.button_finish_time);
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
		    }
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0:
				if(mDataFragment==null) {
					mDataFragment = new DataFragment();
				}
				return mDataFragment;
			case 1:
				if(mMapFragment==null) {
					mMapFragment = new MapFragment();
				}
				return mMapFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1_data);
			case 1:
				return getString(R.string.title_section2_map);
			}
			return null;
		}
	}
	
	private void init() {
		initFragment();
		initButton();
	}

	private void initFragment() {
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	private void initButton() {
		mButtonStarTime = (Button) findViewById(R.id.button_start_time);
		mButtonFinishTime = (Button) findViewById(R.id.button_finish_time);
		mButtonControl = (ImageButton) findViewById(R.id.imageButton_reset);
		mButtonStarTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectDateAndTime(mButtonStarTime);
			}
		});
		mButtonFinishTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectDateAndTime(mButtonFinishTime);
			}
		});
		mButtonControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSelectStartTime!=0 && mSelectFinishTime!=0) {
					HarService.queryHarData(getApplicationContext(), mSelectStartTime, mSelectFinishTime);
				} else {
					Toast.makeText(getApplicationContext(), "Set Time First",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
    	mViewButtons = findViewById(R.id.buttons_history_data);
    	mViewButtons.setVisibility(View.GONE);
	}
	
	private void setHarStatusView(boolean isHarRunning) {
		if(mStartMenuItem!=null && mShowHistoryMenuItem !=null){
			if(isHarRunning){
				mStartMenuItem.setIcon(
						getResources().getDrawable(R.drawable.pause));
				mShowHistoryMenuItem.setEnabled(false);
				mShowHistoryMenuItem.setIcon(
						getResources().getDrawable(R.drawable.show_light));
			} else {
				mStartMenuItem.setIcon(
						getResources().getDrawable(R.drawable.play_dark));
				mShowHistoryMenuItem.setEnabled(true);
				mShowHistoryMenuItem.setIcon(
						getResources().getDrawable(R.drawable.show_dark));
			}
		} else {
			HarService.getHarRunningState(getApplicationContext());
		}
	}
	
	private void setShowHistoryStatusView() {
    	if(!mIsShowHistoryData) {
    		mIsShowHistoryData = true;
    		mShowHistoryMenuItem.setIcon(getResources().getDrawable(R.drawable.cancel));
    		mStartMenuItem.setEnabled(false);
    		mStartMenuItem.setIcon(getResources().getDrawable(R.drawable.play_light));
    		mViewButtons.setVisibility(View.VISIBLE);
    	} else {
    		mIsShowHistoryData = false;
    		mShowHistoryMenuItem.setIcon(getResources().getDrawable(R.drawable.show_dark));
    		mStartMenuItem.setEnabled(true);
    		mStartMenuItem.setIcon(getResources().getDrawable(R.drawable.play_dark));
    		mViewButtons.setVisibility(View.GONE);
    	}
	}

	private void selectDateAndTime(Button btn) {
		final Button clickedButton = btn;
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this); 
        View view = View.inflate(this, R.layout.dialog_date_time, null); 
        final DatePicker datePicker = 
        		(DatePicker) view.findViewById(R.id.date_picker); 
        final TimePicker timePicker = 
        		(TimePicker) view.findViewById(R.id.time_picker); 
        builder.setView(view); 

        Calendar cal = Calendar.getInstance(); 
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
        		cal.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)); 
        timePicker.setCurrentMinute(cal.get(Calendar.MINUTE)); 
        
        builder.setTitle("Select Date And Time"); 
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int which) {
            	// strDateTime form as: 201411200900
            	String strDateTime = String.format("%d%02d%02d%02d%02d",  
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
            	//parse unix timestamp from string
            	long datetime = StrUtil.parseTime(strDateTime,"yyyyMMddHHmm");
            	//set button text
                if(clickedButton.equals(mButtonStarTime)) {
                	mSelectStartTime = datetime;
                	LogUtil.info("start time:" + mSelectStartTime);
                } else if(clickedButton.equals(mButtonFinishTime)) {
                	mSelectFinishTime = datetime;
                	LogUtil.info("finish time:" + mSelectFinishTime);
                }
				clickedButton.setText(StrUtil.strTime(datetime, "yyyy-MM-dd\nHH:mm"));
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", null);
        Dialog dialog = builder.create();
        dialog.show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleIsHarRunning(HarService.HarRunningStateEvent event) {
		LogUtil.info("MainActivity - handleIsHarRunning()");
		mIsHarRunning = event.isRunning;
		setHarStatusView(mIsHarRunning);
	}
	
	private void showAlertDialogToOpenGPS() {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle(R.string.alert_dialog_title_open_gps);
		builder.setMessage(R.string.alert_dialog_message_open_gps);
		builder.setPositiveButton(
				getString(R.string.alert_dialog_postive),
				new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog,int which) {
		                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                MainActivity.this.startActivity(myIntent);
		            }
		        });
		builder.setNegativeButton(getString(R.string.alert_dialog_negative), null);
		builder.create().show();
	}

}
