package com.rockcode.har.demo;

import java.util.HashMap;
import java.util.List;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.rockcode.har.demo.base.LocationCollector;
import com.rockcode.har.demo.base.UserActivityInfo;
import com.rockcode.har.demo.util.LogUtil;
import com.rockcode.har.demo.util.StrUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MapFragment extends Fragment {

	AMap mAmap = null;
	MapView mMapView = null;
	private Marker mLastClickedMarker;


	HashMap<String, UserActivityInfo> mMarkerActivityMap;

	private static final float DEFAULT_ZOOM = 19;
	
	public MapFragment() {
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// init map
		mMapView = (MapView) getActivity().findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mAmap = mMapView.getMap();
		mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
		mAmap.setOnMarkerClickListener(mOnMarkerClickListener);
		mAmap.setOnMapClickListener(mOnMapClickListener);
		mAmap.setInfoWindowAdapter(mInfoWindowAdapter);
		Location location = LocationCollector.getLastLocation(getActivity().getApplicationContext());
		if (location != null) {
			setMapCenter(location, DEFAULT_ZOOM);
		} else {
			setMapCenter(30.75363117, 103.92886356, DEFAULT_ZOOM);
		}
		mMarkerActivityMap = new HashMap<>();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}
	
	public void setMapCenter(Location location, float zoom) {
		setMapCenter(location.getLatitude(), location.getLongitude(), zoom);
	}

	public void setMapCenter(double latitude, double longitude, float zoom) {
		LatLng pt1 = transGPS2Map(latitude, longitude);
		CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
				new CameraPosition(pt1, zoom, 30, 0));
		mAmap.moveCamera(mCameraUpdate);
	}
	
	public void setMapCenter(double latitude, double longitude) {
		LatLng pt1 = transGPS2Map(latitude, longitude);
		CameraUpdate mCameraUpdate = CameraUpdateFactory.changeLatLng(pt1);
		mAmap.moveCamera(mCameraUpdate);
	}
	
	public void drawMarkers(List<UserActivityInfo> harList) {
		for (UserActivityInfo ha : harList) {
			drawMarker(ha);
		}
		LogUtil.info("MapFragment - Draw "+ harList.size() + " Dots On MAP");
	}

	public void drawMarker(UserActivityInfo data) {
		LatLng point = new LatLng(data.mLatitude, data.mLongitude);
		// add marker to map
		int resId = getResId(data.mActivity);
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(resId);
		MarkerOptions markerOption = new MarkerOptions();
		markerOption.setGps(true);
		markerOption.position(point);
		markerOption.icon(bitmap);
		markerOption.title(data.mActivity + "\n" + StrUtil.strTime(data.mGpsTime, "yyyy-MM-dd HH:mm:ss"));
		mAmap.addMarker(markerOption);
		mMarkerActivityMap.put(markerOption.getTitle(), data);
	}

	private AMap.InfoWindowAdapter mInfoWindowAdapter = new AMap.InfoWindowAdapter() {
		@Override
		public View getInfoWindow(Marker marker) {
			UserActivityInfo data = mMarkerActivityMap.get(marker.getTitle());
			Button button = new Button(getActivity());
			button.setBackgroundResource(R.drawable.popup);
			button.setText(marker.getTitle());
			button.setTextSize(13);
			button.setTag(data);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					UserActivityInfo ha = (UserActivityInfo) v.getTag();
					popupHarDetailDialog(ha);
				}
			});
			return button;
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	};

	private AMap.OnMapClickListener mOnMapClickListener = new AMap.OnMapClickListener() {
		@Override
		public void onMapClick(LatLng latLng) {
			if (mLastClickedMarker != null && mLastClickedMarker.isInfoWindowShown()) {
				mLastClickedMarker.hideInfoWindow();
			}
		}
	};

	private AMap.OnMarkerClickListener mOnMarkerClickListener = new AMap.OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			mLastClickedMarker = marker;
			return false;
		}
	};
	
	public void popupHarDetailDialog(final UserActivityInfo ha) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialog_layout = inflater.inflate(R.layout.dialog_har_detail,
				(ViewGroup) getActivity().findViewById(R.id.dialog));
		//set dialog content
		TextView textViewActivity = (TextView) dialog_layout.findViewById(R.id.textView_activity);
		textViewActivity.setText(ha.mActivity);
		TextView textViewTimestamp = (TextView) dialog_layout.findViewById(R.id.textView_timestamp);
		textViewTimestamp.setText(StrUtil.strTime(ha.mStartTime, "yyyy-MM-dd HH:mm:ss"));
		TextView textViewGPSTime = (TextView) dialog_layout.findViewById(R.id.textView_gpstime);
		textViewGPSTime.setText(StrUtil.strTime(ha.mGpsTime, "yyyy-MM-dd HH:mm:ss"));
		TextView textViewLatitude = (TextView) dialog_layout.findViewById(R.id.textView_latitude);
		textViewLatitude.setText(ha.mLatitude + "");
		TextView textViewLongitude = (TextView) dialog_layout.findViewById(R.id.textView_longitude);
		textViewLongitude.setText(ha.mLongitude + "");
		//dialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.dialog_result_details_title));
		builder.setView(dialog_layout);
		builder.setPositiveButton(getString(R.string.dialog_postive), null);
		Dialog dialog = builder.create();
		dialog.show();
	}
	
	public void clearOverlay() {
		LogUtil.info("Clear All The Overlay On MAP");
		mAmap.clear();
		mMarkerActivityMap.clear();
	}
	
	public LatLng transGPS2Map(double lat, double lon) {
		LatLng pt = new LatLng(lat, lon);
		return transGPS2Map(pt);
	}

	public LatLng transGPS2Map(LatLng latLng) {
		CoordinateConverter cc = new CoordinateConverter();
		return cc.from(CoordinateConverter.CoordType.GPS).coord(latLng).convert();
	}
	
	public int getResId(String activity) {
		switch(activity) {
			case UserActivityInfo.ACTIVITY_NOLABEL: 
				return R.drawable.dot_grey_24;		
			case UserActivityInfo.ACTIVITY_WALKING: 
				return R.drawable.dot_green_24;
			case UserActivityInfo.ACTIVITY_JOGGING: 
				return R.drawable.dot_cyan_24;
			case UserActivityInfo.ACTIVITY_CYCLING:
				return R.drawable.dot_orange_24;
			case UserActivityInfo.ACTIVITY_STAIRS: 
				return R.drawable.dot_yellow_24;
			case UserActivityInfo.ACTIVITY_STANDING: 
				return R.drawable.dot_blue_24;
			case UserActivityInfo.ACTIVITY_SITTING: 
				return R.drawable.dot_red_24;
			default:
				return R.drawable.dot_grey_24;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleHarQueryResult(HarService.HarDataQueryResultEvent event) {
		LogUtil.info("MapFragment - handleHarQueryResult");
		clearOverlay();
		List<UserActivityInfo> harDataList = event.harDataList;
		drawMarkers(harDataList);
		UserActivityInfo ha0 = harDataList.get(harDataList.size()-1);
		setMapCenter(ha0.mLatitude, ha0.mLongitude);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleGetRecentHarData(HarService.HarDataRecentResultEvent event) {
		LogUtil.info("MapFragment - handleGetRecentHarData()");
		List<UserActivityInfo> harDataList =  event.harDataList;
		drawMarkers(harDataList);
		UserActivityInfo ha0 = harDataList.get(harDataList.size()-1);
		setMapCenter(ha0.mLatitude, ha0.mLongitude);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleHarUpdate(HarService.HarDataUpdateEvent event) {
		LogUtil.info("MapFragment - handleHarUpdate()");
		UserActivityInfo ha = event.harData;
		drawMarker(ha);
		setMapCenter(ha.mLatitude, ha.mLongitude);
	}

}
