package com.rockcode.har.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Location;

public class StrUtil {

	
	/**
	 * 
	 * @param timestamp : for example yyyyMMdd_HHmmss
	 * @param formats
	 * @return
	 */
	public static String strTime(long timestamp, String formats){ 
		String str = new SimpleDateFormat(formats, Locale.US).
				format(new Date(timestamp));    
		return str;
	}
	
	/**
	 * return time format string as: yyyyMMdd_HHmmss
	 * @param timestamp
	 * @return
	 */
	public static String strTime(long timestamp){ 
		return strTime(timestamp, "yyyyMMdd_HHmmss");
	}
	
	/**
	 * 
	 * @param strTime
	 * @param format : for example: yyyyMMdd_HHmmss
	 * @return
	 */
	public static long parseTime(String strTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		long unixtime = 0;
		try {
			unixtime = sdf.parse(strTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return unixtime;
	}
	
	/**
	 * format of time as: "yyyyMMdd_HHmmss"
	 * @param strTime: "yyyyMMdd_HHmmss"
	 * @return
	 */
	public static long parseTime(String strTime) {
		return parseTime(strTime, "yyyyMMdd_HHmmss");
	}
	
	/**
	 * 
	 * @param location
	 * @return
	 */
	public static String strLocation(Location location) {
		String str="";
		if(location!=null) {
			str += location.getLatitude() + "," + location.getLongitude();			
		} else {
			str += "null,null";
		}
		return str;
	}
	
	/**
	 * return strin format: yyyyMMdd_HHmmss,latitude,longitude
	 * @param location
	 * @return
	 */
	public static String strLocationAndTime(Location location) {
		String str="";
		if(location!=null) {
			str += strTime(location.getTime())
					+ "," + location.getLatitude() 
					+ "," + location.getLongitude();		
		} else {
			str += "null,null,null";
		}
		return str;
	}
	
	/**
	 * return string format: unix timestamp,latitude,longitude
	 * @param location
	 * @return
	 */
	public static String strLocationAndUnixTime(Location location) {
		String str="";
		if(location!=null) {
			str += 	location.getTime()
					+ "," + location.getLatitude() 
					+ "," + location.getLongitude();		
		} else {
			str += "null,null,null";
		}
		return str;
	}
	

}
