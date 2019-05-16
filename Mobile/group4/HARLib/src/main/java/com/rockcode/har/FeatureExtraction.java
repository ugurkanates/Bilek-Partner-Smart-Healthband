package com.rockcode.har;


/**
 * WISDM Lab http://www.cis.fordham.edu/wisdm/
 * 
 * library of functions to transform raw data into features for activity recognition. 
 * 
 * @author Jeff Lockhart <a href="mailto:lockhart@cis.fordham.edu">lockhart@cis.fordham.edu</a>
 * @version 1.2.0
 * @date 25 October 2013
 */

class FeatureExtraction {

	/**
	 * the maximum number of peak values is half of the total values plus 1
	 * because every other value could be a peak
	 */
	private static int maxPeaks = (TupleFeature.mRecordNumber / 2) + 1;

	/**
	 * generic bins array
	 */
	private static double[] generic_bins = {
		-2.5,0,2.5,5,7.5,10,12.5,15,17.5,20,
		-2.5,0,2.5,5,7.5,10,12.5,15,17.5,20,
		-2.5,0,2.5,5,7.5,10,12.5,15,17.5,20
	};

	/**
	 * governs feature generation
	 * @param tup TupleFeature has not been process
	 * @return TupleFeature include feature
	 */
	static TupleFeature processTuple(TupleFeature tup){
		int count = tup.getCount();
		double[] x = TupleFeature.toDoubles(tup.getX());
		double[] y = TupleFeature.toDoubles(tup.getY());
		double[] z = TupleFeature.toDoubles(tup.getZ());
		
		return processTuple(count, tup.getT(), x, y, z, tup, generic_bins); 
	}
	
	/**
	 * governs feature generation
	 * @param tup
	 * @param bins
	 * @return
	 */
	public static TupleFeature processTuple(TupleFeature tup, double[] bins){
		int count = tup.getCount();
		double[] x = TupleFeature.toDoubles(tup.getX());
		double[] y = TupleFeature.toDoubles(tup.getY());
		double[] z = TupleFeature.toDoubles(tup.getZ());
		
		return processTuple(count, tup.getT(), x, y, z, tup, bins); 
	}
	
	/**
	 * governs feature generation
	 * @param count
	 * @param t
	 * @param x
	 * @param y
	 * @param z
	 * @param tup
	 * @param bins
	 * @return
	 */
	static TupleFeature processTuple(int count, long[] t, double[] x,
			double[] y, double[] z, TupleFeature tup, double[] bins){
			float[] floatFeat = new float[43];
	
		double[] xbins = fillBins(count, x, 'x', bins);
		double[] ybins = fillBins(count, y, 'y', bins);
		double[] zbins = fillBins(count, z, 'z', bins);
		
		//accel data
		for (int i = 0; i < 10; i++) {
			floatFeat[i] = (float) xbins[i];
		}
		for (int i = 0; i < 10; i++) {
			floatFeat[10 + i] = (float) ybins[i];
		}
		for (int i = 0; i < 10; i++) {
			floatFeat[20 + i] = (float) zbins[i];
		}

		floatFeat[30] = getAvr(count, x);
		floatFeat[31] = getAvr(count, y);
		floatFeat[32] = getAvr(count, z);
		floatFeat[33] = getPeakTime(t, x);
		floatFeat[34] = getPeakTime(t, y);
		floatFeat[35] = getPeakTime(t, z);
		floatFeat[36] = getAbsDev(count, x, floatFeat[30]);
		floatFeat[37] = getAbsDev(count, y, floatFeat[31]);
		floatFeat[38] = getAbsDev(count, z, floatFeat[32]);
		floatFeat[39] = getStandDiv(count, x, floatFeat[30]);
		floatFeat[40] = getStandDiv(count, y, floatFeat[31]);
		floatFeat[41] = getStandDiv(count, z, floatFeat[32]);
		floatFeat[42] = getAvrMagnitude(count, x, y, z);
	
		// store results to tupfeat object
		tup.setFeatures(floatFeat);
		tup.killRawData(); // save some memory

		return tup;
	}
	
	/**
	 * Bins the data and then generates an array of percents: one representing
	 * the percent of samples falling into each bin.
	 * 
	 * @param count
	 *            number of samples in the array
	 * @param n
	 *            the array
	 * @param axis
	 *            which axis we're looking at, used to select bins
	 * @return an array of %'s
	 */
	static double[] fillBins(int count, double[] n, char axis, double[] bins) {
		double[] binAvgs = new double[10];
		int[] binCounts = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		// holds the index of where to start reading bin values from the bounds
		// array
		int b = 0;
		if (axis == 'x') {
			b = 0;
		} else if (axis == 'y') {
			b = 10;
		} else if (axis == 'z') {
			b = 20;
		}

		// sorts samples into bins & counts them
		for (int i = 0; i < count; i++) {
			if (n[i] < bins[0]) { // anything smaller than the first level goes
									// in the first bin
				binCounts[0]++;
			} else if (n[i] < bins[b + 1]) {
				binCounts[1]++;
			} else if (n[i] < bins[b + 2]) {
				binCounts[2]++;
			} else if (n[i] < bins[b + 3]) {
				binCounts[3]++;
			} else if (n[i] < bins[b + 4]) {
				binCounts[4]++;
			} else if (n[i] < bins[b + 5]) {
				binCounts[5]++;
			} else if (n[i] < bins[b + 6]) {
				binCounts[6]++;
			} else if (n[i] < bins[b + 7]) {
				binCounts[7]++;
			} else if (n[i] < bins[b + 8]) {
				binCounts[8]++;
			} else { // anything larger than the 9th level goes in the 10th bin
				binCounts[9]++;
			}
		}
		// generates percent per bin statistic
		for (int i = 0; i < 10; i++) {
			binAvgs[i] = (double) binCounts[i] / (double) count;
		}

		return binAvgs;
	}

	/**
	 * generates time between peaks for one axis over a 10 second tuple
	 * 
	 * @param t
	 *            the array of time stamps matching n
	 * @param n
	 *            the array of accelerometer values for one axis
	 * @return average time between peaks
	 */
	static float getPeakTime(long[] t, double[] n) {
		double[] allPeaks = new double[maxPeaks];
		long[] peakTimes = new long[maxPeaks], highTimes = new long[maxPeaks];

		double tmp1 = n[0], tmp2 = n[1], tmp3 = n[2];
		int highPeakCount = 0;
		float favr = 0;
		double highest = 0, threshold = 0.9, avr = 0;

		// runs through array and grabs peaks
		for (int i = 3, j = 0; i < (n.length - 2); i++) {

			if (tmp2 > tmp1 && tmp2 > tmp3) {
				allPeaks[j] = tmp2;
				peakTimes[j] = t[i];
				j++;
				if (tmp2 > highest) {// remember the highest peak
					highest = tmp2;
				}
			}

			tmp1 = tmp2;
			tmp2 = tmp3;
			tmp3 = n[i + 1];
		}

		// count peaks above threshold and store their timestamps
		for (int i = 0; i < allPeaks.length; i++) {
			if (allPeaks[i] > threshold * highest) {
				highTimes[highPeakCount] = peakTimes[i];
				highPeakCount++;
			}
		}
		// if not enough peaks are found, the loop executes
		while (highPeakCount < 3 && threshold > 0) {
			// lower the threshold incrementally until enough peaks are found
			threshold -= .05; 
			highPeakCount = 0; // reset to avoid a double count
			
			for (int i = 0; i < allPeaks.length; i++) {
				if (allPeaks[i] > threshold * highest) {
					// if the loop executes, it will write over the old values
					highTimes[highPeakCount] = peakTimes[i];
					highPeakCount++;
				}
			}
		}

		// calcs the actual average time between given peaks
		if (highPeakCount < 3) {
			avr = 0;
		} else {
			for (int i = 0; i < (highPeakCount - 1); i++) {
				// for now avr is the sum of each difference
				avr += (highTimes[i + 1] - highTimes[i]); 
			}
			// avr becomes the average of those differences
			avr = avr / (highPeakCount - 1); 
		}
		favr = (float) avr;
		return favr;
	}

	/**
	 * returns the absolute deviation of a tuple
	 * 
	 * @param count
	 *            number of entries in the tuple
	 * @param n
	 *            the array of entries
	 * @param navr
	 *            the average value of all entries in the array
	 * @return
	 */
	static float getAbsDev(int count, double[] n, float navr) {
		double aDev = 0;
		for (int i = 0; i < count; i++) {
			aDev += Math.abs(n[i] - navr);
		}
		aDev = aDev / count;

		return (float) aDev;
	}

	/**
	 * generates standard deviation statistic for one axis in one 10 second
	 * tuple
	 * 
	 * @param count
	 *            number of (real) entries in the array
	 * @param n
	 *            the array of accelerometer values
	 * @param nAvr
	 *            the average value of the entries in n
	 * @return the standard deviation statistic
	 */
	static float getStandDiv(int count, double[] n, float nAvr) {
		double nSDiv = 0; // standard deviations for each variable
		// generates sums of squares of differences to use an standard deviation
		for (int k = 0; k < count; k++) {
			nSDiv += ((n[k] - nAvr) * (n[k] - nAvr));
		}
		nSDiv = (Math.sqrt(nSDiv)) / count;

		return (float) nSDiv;
	}

	/**
	 * returns the average of an input array's values
	 * 
	 * @param count
	 *            number of real values in the array
	 * @param n
	 *            the array
	 * @return an average in type float
	 */
	static float getAvr(int count, double[] n) {
		float avr = 0;
		for (int i = 0; i < count; i++) {
			avr += n[i];
		}
		avr = avr / count;

		return avr;
	}
	
	/**
	 * returns array of the sum of the absolute values 
	 *	of the 3 axes for each time in the tuple 
	 * 
	 * @param count
	 *            number of entries in the tuple
	 * @param x
	 *            the array of entries for x axis
	 * @param x
	 *            the array of entries for y axis
	 * @param x
	 *            the array of entries for z axis
	 * @return
	 */
	static double[] getAbsSum(int count, double[] x, double[] y, double[] z){
		double [] values = new double[count];	
		for (int i = 0; i < count; i++){
			values[i] = ((Math.abs(x[i])) + (Math.abs(y[i])) + (Math.abs(z[i])));
		}
		
		return values;
	}

	/**
	 * returns the mean of the sum of the absolute values 
	 *	of the 3 axes of a tuple
	 * 
	 * @param count
	 *            number of entries in the tuple
	 * @param x
	 *            the array of entries for x axis
	 * @param x
	 *            the array of entries for y axis
	 * @param x
	 *            the array of entries for z axis
	 * @return
	 */
	static float getAvrAbsSum(int count, double[] x, double[] y, double[] z){
		double[] values = getAbsSum(count, x, y, z);	
		
		return getAvr(count, values);
	}
	
	/**
	 * returns the standard deviation of the sum of the absolute values 
	 *	of the 3 axes of a tuple
	 * 
	 * @param count
	 *            number of entries in the tuple
	 * @param x
	 *            the array of entries for x axis
	 * @param x
	 *            the array of entries for y axis
	 * @param x
	 *            the array of entries for z axis
	 * @return
	 */
	static float getStandDevAbsSum(int count, double[] x, double[] y, double[] z){
		float avr = getAvrAbsSum(count, x, y, z);
		double[] values = getAbsSum(count, x, y, z);
		
		return getStandDiv(count, values, avr);
	}

	/**
	 *returns array of the root of the sum of the squares (magnitude) 
	 *	of the 3 axes for each time in the tuple 
	 * 
	 * @param count
	 *            number of entries in the tuple
	 * @param x
	 *            the array of entries for x axis
	 * @param y
	 *            the array of entries for y axis
	 * @param z
	 *            the array of entries for z axis
	 * @return
	 */
	static double[] getMagnitude(int count, double[] x, double[] y, double[] z){
		double [] values = new double [count];
		double sum = 0;
		for (int i = 0; i < count; i++){
			sum = ((Math.pow(x[i],2)) + (Math.pow(y[i],2)) + (Math.pow(z[i],2)));		
			values[i] = (Math.sqrt(sum));
		}
	
		return values;
	}

	/**
	 * returns the mean of the root of the squares 
	 *	of the 3 axes
	 * 
	 * @param count
	 *            number of entries in the tuple
	 * @param x
	 *            the array of entries for x axis
	 * @param x
	 *            the array of entries for y axis
	 * @param x
	 *            the array of entries for z axis
	 * @return
	 */
	static float getAvrMagnitude(int count, double[] x, double[] y, double[] z){
		double[] values = getMagnitude(count, x, y, z);	
		
		return getAvr(count, values);
	}
}
