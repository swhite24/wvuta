package org.mockup.wvuta;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener {
	private static final String TAG = "WVUTA::MAIN";
	private AlarmManager am;
	private DBHelper dbhelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(TAG, "Main onCreate");

		// setup listeners
		Button prtButton = (Button) findViewById(R.id.mainPRTViewStatusButton);
		prtButton.setOnClickListener(this);
		Button prtReportButton = (Button) findViewById(R.id.mainPRTReportButton);
		prtReportButton.setOnClickListener(this);
		Button busButton = (Button) findViewById(R.id.mainBusButton);
		busButton.setOnClickListener(this);

		// Check if DB needs initialized
		check_DB();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "Main onResume");
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(UpdateService.NOTIF_ID);
		setup_alarms();

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuSettings:
			startActivity(new Intent(this, Prefs.class));
			break;
		case R.id.menuAbout:
			startActivity(new Intent(this, About.class));
			break;
		case R.id.menuHelp:
			break;
		}
		return true;
	}

	/**
	 * Sets up alarms. One for ResetService, which resets values stored in
	 * preferences regarding station statuses to defaults.
	 * 
	 * Other will be for notifications/automatic updating.
	 */
	private void setup_alarms() {

		SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
				Context.MODE_PRIVATE);
		Editor ed = prefs.edit();

		// Setup nightly reset alarm
		if (!prefs.getBoolean("reset_set", false)) {

			am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR, 10);
			cal.set(Calendar.MINUTE, 15);
			cal.set(Calendar.AM_PM, Calendar.PM);
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);

			PendingIntent pintent = PendingIntent.getService(this, 0,
					new Intent(this, AllDownService.class), 0);
			am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, pintent);
			Log.d(TAG, "Set AllDownService Alarm starting at "
					+ Constants.TWEETFORMAT.format(cal.getTime()));

			pintent = PendingIntent.getService(this, 0, new Intent(this,
					AllUpService.class), 0);

			cal.set(Calendar.HOUR, 6);
			cal.set(Calendar.MINUTE, 30);
			cal.set(Calendar.AM_PM, Calendar.AM);
			am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, pintent);
			Log.d(TAG, "Set AllUpService Alarm starting at "
					+ Constants.TWEETFORMAT.format(cal.getTime()));

			ed.putBoolean("reset_set", true);
			ed.commit();

		}

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		PendingIntent pintent = PendingIntent.getService(this, 0, new Intent(
				this, UpdateService.class), 0);
		AlarmManager am2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// setup notification update alarm
		if (settings.getBoolean("UPDATE_PREF", false)
				|| !settings.contains("UPDATE_PREF")) {
			int current = Integer.parseInt(settings.getString(
					"NOTIFICATION_FREQ", "3600")) * 1000;
			int last = prefs.getInt("update_freq", -1);
			if (current != last) {
				// get calendar instance and add frequency offset
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(cal.getTimeInMillis() + current);

				try {
					// set alarm to update
					am2.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
							current, pintent);
				} catch (Exception e) {
					Log.d(TAG, "Error: " + e.getMessage());
					return;
				}
				ed.putInt("update_freq", current);
				ed.commit();

				Log.d(TAG, "Set UpdateService alarm starting at "
						+ Constants.TWEETFORMAT.format(cal.getTime()));
			}
		} else {
			Log.d(TAG, "Canceled UpdateService alarm");
			am2.cancel(pintent);
			ed.remove("update_freq");
			ed.commit();
		}
	}

	/**
	 * Checks to see if current station information is present in preferences.
	 * If not, adds default info.
	 */
	private void check_DB() {
		dbhelper = new DBHelper(getApplicationContext());
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		String[] cols = { Constants.LOCATION_COL };
		Cursor cursor = db.query(Constants.TABLE_NAME, cols, null, null, null,
				null, null);
		if (!cursor.moveToFirst()) {
			Log.d(TAG, "Initializing DB");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
			String time = Constants.TWEETFORMAT.format(cal.getTime());

			ContentValues values = new ContentValues();
			values.put(Constants.LOCATION_COL, "BEECHURST");
			values.put(Constants.STATUS_COL, "Up");
			values.put(Constants.SOURCE_COL, "WVUDOT");
			values.put(Constants.TIME_COL, time);
			db.insert(Constants.TABLE_NAME, null, values);

			values = new ContentValues();
			values.put(Constants.LOCATION_COL, "ENGINEERING");
			values.put(Constants.STATUS_COL, "Up");
			values.put(Constants.SOURCE_COL, "WVUDOT");
			values.put(Constants.TIME_COL, time);
			db.insert(Constants.TABLE_NAME, null, values);

			values = new ContentValues();
			values.put(Constants.LOCATION_COL, "MEDICAL");
			values.put(Constants.STATUS_COL, "Up");
			values.put(Constants.SOURCE_COL, "WVUDOT");
			values.put(Constants.TIME_COL, time);
			db.insert(Constants.TABLE_NAME, null, values);

			values = new ContentValues();
			values.put(Constants.LOCATION_COL, "TOWERS");
			values.put(Constants.STATUS_COL, "Up");
			values.put(Constants.SOURCE_COL, "WVUDOT");
			values.put(Constants.TIME_COL, time);
			db.insert(Constants.TABLE_NAME, null, values);

			values = new ContentValues();
			values.put(Constants.LOCATION_COL, "WALNUT");
			values.put(Constants.STATUS_COL, "Up");
			values.put(Constants.SOURCE_COL, "WVUDOT");
			values.put(Constants.TIME_COL, time);
			db.insert(Constants.TABLE_NAME, null, values);
		}
		db.close();
		dbhelper.close();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainPRTViewStatusButton:
			Intent i = new Intent(this, PRTTab.class);
			startActivity(i);
			break;
		case R.id.mainPRTReportButton:
			Intent j = new Intent(this, PRTReportStatus.class);
			startActivity(j);
			break;
		case R.id.mainBusButton:
			Intent k = new Intent(this, BusWebStatus.class);
			startActivity(k);
			break;

		}

	}
}
