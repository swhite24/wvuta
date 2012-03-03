package org.mockup.wvuta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class PRTReportStatus extends Activity implements OnClickListener {
	private SharedPreferences reportTracker;

	private boolean upDown = false;
	private boolean location = false;

	private String statusString = null;
	private String locString = null;

	private TextView reportText;
	private ReportingReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prtreportstatus);

		// initialize preferences
		reportTracker = getSharedPreferences(Constants.TABLENAME,
				Context.MODE_PRIVATE);

		// set clicklisteners for all views in layout
		reportText = (TextView) findViewById(R.id.PRTReportStatusLastReport);
		View submit = findViewById(R.id.prtSubmitButton);
		submit.setOnClickListener(this);

		View downButton = findViewById(R.id.downRB);
		View runningButton = findViewById(R.id.runningRB);
		View beechurstButton = findViewById(R.id.beechurstRB);
		View walnutButton = findViewById(R.id.walnutRB);
		View engineeringButton = findViewById(R.id.engineeringRB);
		View towersButton = findViewById(R.id.towersRB);
		View medicalButton = findViewById(R.id.medicalRB);

		downButton.setOnClickListener(this);
		runningButton.setOnClickListener(this);
		beechurstButton.setOnClickListener(this);
		walnutButton.setOnClickListener(this);
		engineeringButton.setOnClickListener(this);
		towersButton.setOnClickListener(this);
		medicalButton.setOnClickListener(this);

		// set initial text display
		setNewReportText();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Setup receiver to know when ReportingService completes task
		IntentFilter filter = new IntentFilter(ReportingService.REPORTING);
		receiver = new ReportingReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister receiver when activity in background
		unregisterReceiver(receiver);
	}

	private void setNewReportText() {
		if (reportTracker.contains(Constants.TIME)) {
			long lastTime = reportTracker.getLong(Constants.TIME, -1);
			long deltaTime = System.currentTimeMillis() - lastTime;
			int minutesAgo = (int) (deltaTime / 60 / 1000);
			String newText = "unknown";
			if (minutesAgo <= 0) {
				newText = "Last report was 1 minute ago";
			} else if (minutesAgo < 60 && minutesAgo > 0) {
				newText = "Last report was " + (minutesAgo + 1)
						+ " minutes ago";
			} else {
				newText = "Last report was greater than one hour ago";
			}
			reportText.setText(newText);
		}
	}

	public void onClick(View v) {
		Editor editor = reportTracker.edit();
		switch (v.getId()) {
		case R.id.downRB:
			upDown = true;
			statusString = "down";
			break;
		case R.id.runningRB:
			upDown = true;
			statusString = "up";
			break;
		case R.id.beechurstRB:
			location = true;
			locString = "beechurst";
			break;
		case R.id.walnutRB:
			location = true;
			locString = "walnut";
			break;
		case R.id.engineeringRB:
			location = true;
			locString = "engineering";
			break;
		case R.id.towersRB:
			location = true;
			locString = "towers";
			break;
		case R.id.medicalRB:
			location = true;
			locString = "medical";
			break;
		case R.id.prtSubmitButton:
			// status & location selected = accept
			if (upDown && location) {
				long currentTime = System.currentTimeMillis();
				long deltaTime = currentTime
						- (reportTracker.getLong(Constants.TIME, 0));
				// for use with limiting reports per hour
				if (deltaTime < 5000) {
					Toast toast = Toast.makeText(this,
							R.string.submissionLimitExceededText,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					Log.d("submitting", "in here");
					editor.putLong(Constants.TIME, System.currentTimeMillis());
					editor.putString(Constants.STATUS, statusString);
					editor.putString(Constants.LOCATION, locString);
					editor.commit();
					reportText.setText("Submitting...");
					sendReportToDB();
				}
				// one or none of status & location selected = deny
			} else {
				Toast toast = Toast.makeText(this, R.string.denySubmissionText,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			break;
		}
	}

	private Intent serviceIntent;

	public boolean sendReportToDB() {
		// start Reporting Service with selected criteria
		serviceIntent = new Intent(this, ReportingService.class);
		startService(serviceIntent);
		return true;
	}

	public void acceptToast() {
		if (sendReportToDB()) {
			Toast toast = Toast.makeText(this, R.string.acceptedSubmissionText,
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	public class ReportingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// update text
			setNewReportText();

			// create a toast to let user know submission was successful
			acceptToast();
		}

	}
}
