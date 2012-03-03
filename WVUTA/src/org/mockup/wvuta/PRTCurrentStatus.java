package org.mockup.wvuta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class PRTCurrentStatus extends MapActivity implements OnClickListener {

	private ListView currentText;
	private final ArrayList<Report> reportArray = new ArrayList<Report>();
	private RowAdapter rowAdapter = null;
	private ViewGroup header = null;
	private ViewSwitcher switcher = null;
	private MapView map = null;
	private MapController mc = null;
	private PRTStationOverlay overlay = null;
	private Button mapbutton, reportsbutton;
	private Handler handler = new Handler();
	private ReportReceiver receiver;
	private SharedPreferences prefs;
	private Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prtcurrentstatus);

		// initialize preferences & editor
		// also remove previous filters
		prefs = getSharedPreferences(Constants.TABLENAME, Context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.remove("location");
		editor.remove("status");
		editor.commit();

		// initialize listview & customized arrayadapter
		currentText = (ListView) findViewById(R.id.displayList);
		rowAdapter = new RowAdapter(this, R.layout.rowcustom, reportArray);
		addHeader();
		currentText.setAdapter(rowAdapter);

		switcher = (ViewSwitcher) findViewById(R.id.switcher);

		map = new MapView(PRTCurrentStatus.this, Constants.RELEASE_APIKEY);
		Thread thread = new Thread(null, background, "background");
		thread.start();

		// init buttons
		mapbutton = (Button) findViewById(R.id.currentMapButton);
		reportsbutton = (Button) findViewById(R.id.currentReportButton);
		mapbutton.setOnClickListener(this);
		reportsbutton.setOnClickListener(this);

		// what happens when individual report is clicked
		currentText.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				if (position != 0) {
					AlertDialog.Builder adb = new AlertDialog.Builder(
							PRTCurrentStatus.this);
					StringBuilder output = new StringBuilder();
					Report source = (Report) currentText
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

		// automatically update
		SharedPreferences shared_prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean auto_update = shared_prefs.getBoolean("UPDATE_PREF", false);
		if (auto_update) {
			getDBInfo();
		}
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
						Log.e("layoutinflater", e.toString());
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

	private void initMap() {
		map.setClickable(true);
		mc = map.getController();
		// points display on map
		GeoPoint beechurst = new GeoPoint(Constants.bLat.intValue(),
				Constants.bLon.intValue());
		GeoPoint engineering = new GeoPoint(Constants.eLat.intValue(),
				Constants.eLon.intValue());
		GeoPoint medical = new GeoPoint(Constants.mLat.intValue(),
				Constants.mLon.intValue());
		GeoPoint towers = new GeoPoint(Constants.tLat.intValue(),
				Constants.tLon.intValue());
		GeoPoint walnut = new GeoPoint(Constants.wLat.intValue(),
				Constants.wLon.intValue());

		// center of map
		int centerLat = (Constants.mLat.intValue() + Constants.wLat.intValue()) / 2;
		int centerLon = (Constants.eLon.intValue() + Constants.bLon.intValue()) / 2;
		mc.setCenter(new GeoPoint(centerLat, centerLon));
		mc.setZoom(15);

		// add points to overlay to be drawn		
		Bitmap temp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(temp);
		RectF to_draw = new RectF(0, 0, 10, 10);
		Paint backpaint = new Paint();
		backpaint.setARGB(175, 0, 0, 128);
		backpaint.setAntiAlias(true);
		canvas.drawOval(to_draw, backpaint);
		
		Drawable d = new BitmapDrawable(temp);
		
		overlay = new PRTStationOverlay(d, this);
		overlay.addPoint(new OverlayItem(beechurst, "Beechurst",
				"Beechurst Station Details"));
		overlay.addPoint(new OverlayItem(engineering, "Engineering",
				"Engineering station details"));
		overlay.addPoint(new OverlayItem(medical, "Medical",
				"Medical station details"));
		overlay.addPoint(new OverlayItem(towers, "Towers",
				"Towers station details"));
		overlay.addPoint(new OverlayItem(walnut, "Walnut",
				"Walnut station details"));
		
		// add overlay to map
		List<Overlay> list = map.getOverlays();
		list.add(overlay);
		handler.postDelayed(addToSwitcher, 1000);
	}

	private Runnable background = new Runnable() {
		public void run() {
			initMap();
		}
	};

	private Runnable addToSwitcher = new Runnable() {
		public void run() {
			addMap();
		}
	};

	private void addMap() {
		switcher.addView(map);
	}

	private Intent serviceIntent;

	public void getDBInfo() {
		defaultText();
		serviceIntent = new Intent(this, RetrievingService.class);
		startService(serviceIntent);
	}

	public void updateInfo() {
		// update listview with most recent results
		currentText.setAdapter(rowAdapter);
	}

	public void defaultText() {
		// sets text in listview to something to let user know system is working
		reportArray.clear();
		Report temp = new Report("...Retrieving data from server...", null,
				null);
		reportArray.add(temp);
		rowAdapter = new RowAdapter(PRTCurrentStatus.this, R.layout.rowcustom,
				reportArray);
		updateInfo();
	}

	public void addHeader() {
		// add headers to the three columns in listview
		LayoutInflater inflater = getLayoutInflater();
		header = (ViewGroup) inflater.inflate(R.layout.listheader, currentText,
				false);
		currentText.addHeaderView(header);
	}

	public class ReportReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
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
			rowAdapter = new RowAdapter(PRTCurrentStatus.this,
					R.layout.rowcustom, reportArray);
			updateInfo();
		}

	}

	public void onClick(View v) {
		if (switcher.getNextView() != null) {
			switch (v.getId()) {
			case R.id.currentMapButton:
				if (switcher.getNextView().getId() != R.id.displayList) {
					switcher.showNext();
				}
				break;
			case R.id.currentReportButton:
				if (switcher.getNextView().getId() == R.id.displayList) {
					switcher.showNext();
				}
				break;
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
