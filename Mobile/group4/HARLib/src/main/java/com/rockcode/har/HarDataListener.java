package com.rockcode.har;

import java.util.List;

/**
 * Listen to HAR data change
 */
public interface HarDataListener {

	/**
	 * on HAR given an recognition result
	 * @param ha recognition result
	*/
	void onHarDataChange(HumanActivity ha);

	/**
	 * on HAR collect the sensor data for a period of time
	 * @param rawdataList raw sensor data list
	*/
	void onHarRawDataChange(List<RawData> rawdataList);

}
