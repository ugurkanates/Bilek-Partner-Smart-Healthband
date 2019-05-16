package com.rockcode.har;

import java.util.List;

/**
 * Listen to SensorDataCollector finish one cycle data collect
 */
interface SensorDataListener {

	/**
	 * On SensorDataCollector finish one cycle data collect
	 * @param rawDataList Collected raw sensor data list
	 */
	void onSensorDataChange(List<RawData> rawDataList);

}
