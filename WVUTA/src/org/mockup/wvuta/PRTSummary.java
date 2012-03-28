package org.mockup.wvuta;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
	private StatusReceiver receiver;
	private ProgressDialog p_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prtsummary);
		Log.d(TAG, "PRTSummary onCreate");

		beech = (TextView) findViewById(R.id.prtsummary_beech_status);
		eng = (TextView) findViewById(R.id.prtsummary_eng_status);
		med = (TextView) findViewById(R.id.prtsummary_med_status);
		tow = (TextView) findViewById(R.id.prtsummary_tow_status);
		wal = (TextView) findViewById(R.id.prtsummary_wal_status);

		refresh_button = (Button) findViewById(R.id.prtsummary_rfrsh_btn);
		refresh_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(PROGRESS);
				Intent i = new Intent(PRTSummary.this, RetrievingService.class);
				startService(i);
			}
		});

		populate_fields();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(RetrievingService.REPORTS);
		receiver = new StatusReceiver();
		registerReceiver(receiver, filter);
		populate_fields();
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
		SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
				Context.MODE_PRIVATE);

		String beech_text = prefs.getString(Constants.BEECHURST, null);
		String eng_text = prefs.getString(Constants.ENGINEERING, null);
		String med_text = prefs.getString(Constants.MEDICAL, null);
		String tow_text = prefs.getString(Constants.TOWERS, null);
		String wal_text = prefs.getString(Constants.WALNUT, null);

		if (beech_text != null) {
			beech.setText(beech_text);
			if (beech_text.equalsIgnoreCase("up")) {
				beech.setTextColor(Color.GREEN);
			} else if (beech_text.equalsIgnoreCase("down")) {
				beech.setTextColor(Color.RED);
			}
		}
		if (eng_text != null) {
			eng.setText(eng_text);
			if (eng_text.equalsIgnoreCase("up")) {
				eng.setTextColor(Color.GREEN);
			} else if (eng_text.equalsIgnoreCase("down")) {
				eng.setTextColor(Color.RED);
			}
		}
		if (med_text != null) {
			med.setText(med_text);
			if (med_text.equalsIgnoreCase("up")) {
				med.setTextColor(Color.GREEN);
			} else if (med_text.equalsIgnoreCase("down")) {
				med.setTextColor(Color.RED);
			}
		}
		if (tow_text != null) {
			tow.setText(tow_text);
			if (tow_text.equalsIgnoreCase("up")) {
				tow.setTextColor(Color.GREEN);
			} else if (tow_text.equalsIgnoreCase("down")) {
				tow.setTextColor(Color.RED);
			}
		}
		if (wal_text != null) {
			wal.setText(wal_text);
			if (wal_text.equalsIgnoreCase("up")) {
				wal.setTextColor(Color.GREEN);
			} else if (wal_text.equalsIgnoreCase("down")) {
				wal.setTextColor(Color.RED);
			}
		}
	}

	/**
	 * BroadcastReceiver to be notified when latest statuses have been obtained.
	 * Calls populated_fields() when broadcast is received.
	 * 
	 * @author Steve
	 * 
	 */
	private class StatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (p_dialog != null) {
				Log.d(TAG, "Received broadcast");
				if (p_dialog.isShowing())
					dismissDialog(PROGRESS);
				populate_fields();
			}
		}

	}

}
