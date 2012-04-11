package org.mockup.wvuta;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PRTSummary extends Activity {

	private static final String TAG = "WVUTA::PRTSUMMARY";
	static final int PROGRESS = 0;
	private TextView beech, eng, med, tow, wal;
	private Button refresh_button;
	private ReportReceiver report_receiver;
	private TweetReceiver tweet_receiver;
	private ProgressDialog p_dialog;
	private DBHelper dbhelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prtsummary);
		Log.d(TAG, "PRTSummary onCreate");

		dbhelper = new DBHelper(getApplicationContext());

		beech = (TextView) findViewById(R.id.prtsummary_beech_status);
		eng = (TextView) findViewById(R.id.prtsummary_eng_status);
		med = (TextView) findViewById(R.id.prtsummary_med_status);
		tow = (TextView) findViewById(R.id.prtsummary_tow_status);
		wal = (TextView) findViewById(R.id.prtsummary_wal_status);

		refresh_button = (Button) findViewById(R.id.prtsummary_rfrsh_btn);
		refresh_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(PROGRESS);
				Intent i = new Intent(PRTSummary.this, ReportService.class);
				startService(i);
			}
		});

		populate_fields();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "PRTSummary onPause");
		unregisterReceiver(report_receiver);
		unregisterReceiver(tweet_receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ReportService.REPORTS);
		IntentFilter filter1 = new IntentFilter(TweetService.TWEETS);
		report_receiver = new ReportReceiver();
		tweet_receiver = new TweetReceiver();
		registerReceiver(report_receiver, filter);
		registerReceiver(tweet_receiver, filter1);
		populate_fields();
		dbhelper.close();
		super.onResume();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS:
			p_dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
			p_dialog.setMessage("Retrieving latest station details...");
			return p_dialog;
		default:
			return null;
		}
	}

	/**
	 * Retrieves most recent results from preferences and populates TextViews
	 * accordingly.
	 */
	private void populate_fields() {
		String beech_text = null;
		String eng_text = null;
		String med_text = null;
		String tow_text = null;
		String wal_text = null;

		String b_source = null;
		String e_source = null;
		String m_source = null;
		String t_source = null;
		String w_source = null;

		// get latest reports from db
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String[] cols = { Constants.LOCATION_COL, Constants.STATUS_COL,
				Constants.TIME_COL, Constants.SOURCE_COL };
		String orderBy = Constants.LOCATION_COL + " ASC";

		Cursor cursor = db.query(Constants.TABLENAME, cols, null, null, null,
				null, orderBy);

		cursor.moveToNext();
		beech_text = cursor.getString(1);
		b_source = cursor.getString(3);
		cursor.moveToNext();
		eng_text = cursor.getString(1);
		e_source = cursor.getString(3);
		cursor.moveToNext();
		med_text = cursor.getString(1);
		m_source = cursor.getString(3);
		cursor.moveToNext();
		tow_text = cursor.getString(1);
		t_source = cursor.getString(3);
		cursor.moveToNext();
		wal_text = cursor.getString(1);
		w_source = cursor.getString(3);

		// update each station
		if (beech_text != null) {
			if (beech_text.equalsIgnoreCase("up")) {
				beech.setTextColor(Color.GREEN);
				beech.setText(beech_text);
			} else if (beech_text.equalsIgnoreCase("down")) {
				if (b_source == null || !b_source.equals("WVUDOT")) {
					beech.setTextColor(Color.YELLOW);
					beech.setText("Caution");
				} else {
					beech.setTextColor(Color.RED);
					beech.setText(beech_text);
				}
			} else {
				beech.setTextColor(Color.WHITE);
				beech.setText(beech_text);
			}
		}
		if (eng_text != null) {
			if (eng_text.equalsIgnoreCase("up")) {
				eng.setTextColor(Color.GREEN);
				eng.setText(eng_text);
			} else if (eng_text.equalsIgnoreCase("down")) {
				if (e_source == null || !e_source.equals("WVUDOT")) {
					eng.setTextColor(Color.YELLOW);
					eng.setText("Caution");
				} else {
					eng.setTextColor(Color.RED);
					eng.setText(eng_text);
				}
			} else {
				eng.setTextColor(Color.WHITE);
				eng.setText(eng_text);
			}
		}
		if (med_text != null) {
			if (med_text.equalsIgnoreCase("up")) {
				med.setTextColor(Color.GREEN);
				med.setText(med_text);
			} else if (med_text.equalsIgnoreCase("down")) {
				if (m_source == null || !m_source.equals("WVUDOT")) {
					med.setTextColor(Color.YELLOW);
					med.setText("Caution");
				} else {
					med.setTextColor(Color.RED);
					med.setText(med_text);
				}
			} else {
				med.setTextColor(Color.WHITE);
				med.setText(med_text);
			}
		}
		if (tow_text != null) {
			if (tow_text.equalsIgnoreCase("up")) {
				tow.setTextColor(Color.GREEN);
				tow.setText(tow_text);
			} else if (tow_text.equalsIgnoreCase("down")) {
				if (t_source == null || !t_source.equals("WVUDOT")) {
					tow.setTextColor(Color.YELLOW);
					tow.setText("Caution");
				} else {
					tow.setTextColor(Color.RED);
					tow.setText(tow_text);
				}
			} else {
				tow.setTextColor(Color.WHITE);
				tow.setText(tow_text);
			}
		}
		if (wal_text != null) {
			if (wal_text.equalsIgnoreCase("up")) {
				wal.setTextColor(Color.GREEN);
				wal.setText(wal_text);
			} else if (wal_text.equalsIgnoreCase("down")) {
				if (w_source == null || !w_source.equals("WVUDOT")) {
					wal.setTextColor(Color.YELLOW);
					wal.setText("Caution");
				} else {
					wal.setTextColor(Color.RED);
					wal.setText(wal_text);
				}
			} else {
				wal.setTextColor(Color.WHITE);
				wal.setText(wal_text);
			}
		}
		db.close();
	}

	/**
	 * BroadcastReceiver to be notified when latest reports have been obtained.
	 * Starts TweetService on completion.
	 * 
	 * @author Steve
	 * 
	 */
	private class ReportReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (p_dialog != null) {
				Log.d(TAG, "Received reports broadcast");
				PRTSummary.this.startService(new Intent(PRTSummary.this,
						TweetService.class));
			}
		}
	}

	/**
	 * BroadcastReceiver to be notified when latest tweets have been obtained.
	 * Calls populat_fields() on completion.
	 * 
	 * @author Steve
	 * 
	 */
	private class TweetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (p_dialog != null) {
				Log.d(TAG, "Received tweets broadcast");
				if (p_dialog.isShowing())
					dismissDialog(PROGRESS);
				populate_fields();
			}
		}

	}

}
