package org.mockup.wvuta;

import java.text.SimpleDateFormat;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
	public static final String STATUS = "Status";
	public static final String LOCATION = "Location";
	public static final String TIME = "Time";

	public static final String BEECHURST = "Beechurst";
	public static final String WALNUT = "Walnut";
	public static final String ENGINEERING = "Engineering";
	public static final String TOWERS = "Towers";
	public static final String MEDICAL = "Medical";

	public static final String TABLE_NAME = "CURRENT_STATUS_DB";
	public static final String LOCATION_COL = "LOCATION";
	public static final String STATUS_COL = "STATUS";
	public static final String SOURCE_COL = "SOURCE";
	public static final String TIME_COL = "REPORT_TIME";

	public static final String CURRENT_STATUS_TAG = "CurrentStatus";

	public static final String DEBUG_APIKEY = "0UZSL9KnUUNFbu6WhewV9dXo3VH_JoW5Fwp32Yg";
	public static final String RELEASE_APIKEY = "0UZSL9KnUUNH3N7R9sbSde352u9sOZNIb8yQyqA";

	public static final Double bLat = 39.63493025041322 * 1E6;
	public static final Double bLon = -79.95609551668167 * 1E6;

	public static final Double eLat = 39.64716653968941 * 1E6;
	public static final Double eLon = -79.97282579541206 * 1E6;

	public static final Double mLat = 39.65487727496223 * 1E6;
	public static final Double mLon = -79.95998471975327 * 1E6;

	public static final Double tLat = 39.64755274399191 * 1E6;
	public static final Double tLon = -79.9692665040493 * 1E6;

	public static final Double wLat = 39.630057797004916 * 1E6;
	public static final Double wLon = -79.95726093649864 * 1E6;

	public static final String LATEST = "LATEST";

	public static final SimpleDateFormat TWEETFORMAT = new SimpleDateFormat(
			"(MMM. dd @ hh:mmaa)");

}
