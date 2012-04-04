package org.mockup.wvuta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "WVUTA::ALARMRECEIVER";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Alarm received - starting update");
		Intent service_intent = new Intent(context, ReportService.class);
		context.startService(service_intent);
	}

}
