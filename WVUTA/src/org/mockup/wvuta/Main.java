package org.mockup.wvuta;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

		check_maps();
		setup_alarms();
	}	

	@Override
	protected void onResume() {
		Log.d(TAG, "Main onResume");
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
			cal.set(Calendar.HOUR, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.AM_PM, Calendar.AM);
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);

			PendingIntent pintent = PendingIntent.getService(this, 0,
					new Intent(this, ResetService.class), 0);
			am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, pintent);

			ed.putBoolean("reset_set", true);
			ed.commit();

			Log.d(TAG, "Set ResetService Alarm starting at "
					+ Constants.TWEETFORMAT.format(cal.getTime()));
		}

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		PendingIntent pintent = PendingIntent.getService(this, 0, new Intent(
				this, UpdateService.class), 0);
		AlarmManager am2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// setup notification update alarm
		if (settings.getBoolean("NOTIFICATION_PREF", false)
				|| !settings.contains("NOTIFICATION_PREF")) {
			int current = Integer.parseInt(settings.getString(
					"NOTIFICATION_FREQ", "3600")) * 1000;
			int last = prefs.getInt("update_freq", -1);
			if (current != last) {
				Calendar cal = Calendar.getInstance();
				Log.d(TAG, "cal: " + cal.getTimeInMillis());
				cal.setTimeInMillis(cal.getTimeInMillis() + current);

				Log.d(TAG, "cal: " + cal.getTimeInMillis());
				Log.d(TAG, "current: " + current);
				try {
					am2.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
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
	private void check_maps() {
		Log.d(TAG, "Initializing prefs");
		SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
				Context.MODE_PRIVATE);
		if (!prefs.contains(Constants.BEECHURST)
				|| prefs.getString(Constants.BEECHURST, null) == null) {
			Editor ed = prefs.edit();
			ed.putString(Constants.BEECHURST, "Up");
			ed.putString(Constants.ENGINEERING, "Up");
			ed.putString(Constants.MEDICAL, "Up");
			ed.putString(Constants.TOWERS, "Up");
			ed.putString(Constants.WALNUT, "Up");
			ed.putString("bsource", "user");
			ed.putString("esource", "user");
			ed.putString("msource", "user");
			ed.putString("tsource", "user");
			ed.putString("wsource", "user");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			ed.putString("btime", Constants.TWEETFORMAT.format(cal.getTime()));
			ed.putString("etime", Constants.TWEETFORMAT.format(cal.getTime()));
			ed.putString("mtime", Constants.TWEETFORMAT.format(cal.getTime()));
			ed.putString("ttime", Constants.TWEETFORMAT.format(cal.getTime()));
			ed.putString("wtime", Constants.TWEETFORMAT.format(cal.getTime()));

			ed.commit();
		}
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
