package org.mockup.wvuta;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PRTReports extends Activity {
	private static final String TAG = "WVUTA::PRTREPORTS";
	private ListView reports_lv;
	private final ArrayList<Report> reportArray = new ArrayList<Report>();
	private RowAdapter rowAdapter = null;
	private ViewGroup header = null;
	private SharedPreferences prefs;
	private Editor editor;
	private ReportReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prtreports);
		// initialize preferences & editor
		// also remove previous filters
		prefs = getSharedPreferences(Constants.TABLENAME, Context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.remove("location");
		editor.remove("status");
		editor.commit();

		// initialize listview & customized arrayadapter
		reports_lv = (ListView) findViewById(R.id.reportList);
		rowAdapter = new RowAdapter(this, R.layout.rowcustom, reportArray);
		addHeader();
		reports_lv.setAdapter(rowAdapter);

		// what happens when individual report is clicked
		reports_lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				if (position != 0) {
					AlertDialog.Builder adb = new AlertDialog.Builder(
							PRTReports.this);
					StringBuilder output = new StringBuilder();
					Report source = (Report) reports_lv
							.getItemAtPosition(position);
					output.append("Source:  " + source.getNote() + "\n");
					output.append("Location:  " + source.getLocation() + "\n");
					output.append("Status:  " + source.getStatus() + "\n");
					output.append("Time:  " + source.getTime() + "\n");
					adb.setTitle("Report Summary");
					adb.setMessage(output.toString());
					adb.setPositiveButton("Done", null);
					adb.show();
				}
			}
		});
		
		getDBInfo();
	}

	@Override
	public void onResume() {
		// register receiver to receive broadcast from RetrievingService
		IntentFilter filter = new IntentFilter(RetrievingService.REPORTS);
		receiver = new ReportReceiver();
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	public void onPause() {
		// unregister receiver when activity in background
		unregisterReceiver(receiver);
		super.onPause();
	}
	
	/**
	 * Add headers to the three columns in ListView
	 */
	private void addHeader() {
		LayoutInflater inflater = getLayoutInflater();
		header = (ViewGroup) inflater.inflate(R.layout.listheader, reports_lv,
				false);
		reports_lv.addHeaderView(header);
	}

	private Intent serviceIntent;

	/**
	 * Sets default text and starts service to retrieve latest reports from server.
	 */
	private void getDBInfo() {
		Log.d(TAG, "Retrieving latest reports");
		defaultText();
		serviceIntent = new Intent(this, RetrievingService.class);
		startService(serviceIntent);
	}

	/**
	 * Sets text in ListView to something to let user know system is working
	 */
	private void defaultText() {
		reportArray.clear();
		Report temp = new Report("...Retrieving data from server...", null,
				null);
		reportArray.add(temp);
		rowAdapter = new RowAdapter(PRTReports.this, R.layout.rowcustom,
				reportArray);
		updateInfo();
	}

	/**
	 * Update ListView with most recent results
	 */
	private void updateInfo() {
		reports_lv.setAdapter(rowAdapter);
	}

	// menu creation / setup
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		getLayoutInflater().setFactory(new Factory() {
			public View onCreateView(String name, Context context,
					AttributeSet attrs) {
				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
					try {
						LayoutInflater inflater = getLayoutInflater();
						final View view = inflater
								.createView(name, null, attrs);
						new Handler().post(new Runnable() {
							public void run() {
								((TextView) view).setTextSize(10);
								((TextView) view).setTextColor(R.color.black);
							}
						});
						return view;
					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}
				}
				return null;
			}
		});
		inflater.inflate(R.menu.currentstatusmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.currentStatusMenuRefresh:
			editor.remove("location");
			editor.remove("status");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuFBS:
			break;
		case R.id.currentStatusMenuFBL:
			break;
		case R.id.currentStatusMenuDefault:
			editor.remove("location");
			editor.remove("status");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuDown:
			editor.putString("status", "down");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuUp:
			editor.putString("status", "up");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuBeechurst:
			editor.putString("location", "beechurst");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuEngineering:
			editor.putString("location", "engineering");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuMedical:
			editor.putString("location", "medical");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuTowers:
			editor.putString("location", "towers");
			editor.commit();
			getDBInfo();
			break;
		case R.id.currentStatusMenuWalnut:
			editor.putString("location", "walnut");
			editor.commit();
			getDBInfo();
			break;
		}
		return true;
	}
	/**
	 * BroadcastReceiver to receive results from RetrievingService.  Extracts
	 * String ArrayList and compiles it into a list of reports.
	 * @author Steve
	 *
	 */
	public class ReportReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Received broadcast");
			// stopService(serviceIntent);
			// clear last result
			reportArray.clear();
			// get ArrayList of current result
			ArrayList<String> temp = intent.getStringArrayListExtra("strings");
			Iterator<String> i1 = temp.iterator();

			// every three strings represent a new report
			while (i1.hasNext()) {
				String status = i1.next();
				String time = i1.next();
				String location = i1.next();
				reportArray.add(new Report(location, time, status));
			}

			// re-initialize rowAdapter with updated Reports
			rowAdapter = new RowAdapter(PRTReports.this,
					R.layout.rowcustom, reportArray);
			updateInfo();
		}

	}
}
