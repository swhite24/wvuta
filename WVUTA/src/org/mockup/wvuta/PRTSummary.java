package org.mockup.wvuta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PRTSummary extends Activity{

	private static final String TAG = "WVUTA::PRTSUMMARY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView temp = new TextView(this);
		temp.setText("Coming Soon...");
		setContentView(temp);
		
		Log.d(TAG, "PRTSummary onCreate");
	}

	
}
