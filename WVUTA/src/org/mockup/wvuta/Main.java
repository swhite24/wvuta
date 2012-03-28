package org.mockup.wvuta;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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

	private void check_maps() {
		SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
				Context.MODE_PRIVATE);
		if (!prefs.contains(Constants.BEECHURST)){
			Editor ed = prefs.edit();
			ed.putString(Constants.BEECHURST, "Unknown");
			ed.putString(Constants.ENGINEERING, "Unknown");
			ed.putString(Constants.MEDICAL, "Unknown");
			ed.putString(Constants.TOWERS, "Unknown");
			ed.putString(Constants.WALNUT, "Unknown");
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
