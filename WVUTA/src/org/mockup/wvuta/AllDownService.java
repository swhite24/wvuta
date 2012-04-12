package org.mockup.wvuta;

import java.util.Calendar;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class AllDownService extends Service{

	private static final String TAG = "WVUTA::ALLDOWNSERVICE";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "AllDownService onCreate");
		
		DBHelper dbhelper = new DBHelper(getApplicationContext());
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		Calendar cal = Calendar.getInstance();
		String time = Constants.TWEETFORMAT.format(cal.getTime());
		
		ContentValues values = new ContentValues();
		values.put(Constants.LOCATION_COL, "BEECHURST");
		values.put(Constants.STATUS_COL, "Down");
		values.put(Constants.TIME_COL, time);
		values.put(Constants.SOURCE_COL, "WVUDOT");		
		db.replace(Constants.TABLE_NAME, null, values);
		
		values = new ContentValues();
		values.put(Constants.LOCATION_COL, "ENGINEERING");
		values.put(Constants.STATUS_COL, "Down");
		values.put(Constants.TIME_COL, time);
		values.put(Constants.SOURCE_COL, "WVUDOT");		
		db.replace(Constants.TABLE_NAME, null, values);

		values = new ContentValues();
		values.put(Constants.LOCATION_COL, "MEDICAL");
		values.put(Constants.STATUS_COL, "Down");
		values.put(Constants.TIME_COL, time);
		values.put(Constants.SOURCE_COL, "WVUDOT");		
		db.replace(Constants.TABLE_NAME, null, values);

		values = new ContentValues();
		values.put(Constants.LOCATION_COL, "TOWERS");
		values.put(Constants.STATUS_COL, "Down");
		values.put(Constants.TIME_COL, time);
		values.put(Constants.SOURCE_COL, "WVUDOT");		
		db.replace(Constants.TABLE_NAME, null, values);

		values = new ContentValues();
		values.put(Constants.LOCATION_COL, "WALNUT");
		values.put(Constants.STATUS_COL, "Down");
		values.put(Constants.TIME_COL, time);
		values.put(Constants.SOURCE_COL, "WVUDOT");		
		db.replace(Constants.TABLE_NAME, null, values);
		
		Log.d(TAG, "Finished reset.");
		
		db.close();
		dbhelper.close();
	
		stopSelf();
	}

	
}
