package org.mockup.wvuta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BusTweets extends Activity{

	private static final String TAG = "WVUTA::BUSTWEETS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bustweets);
		
		Log.d(TAG, "BusTweets onCreate");
	}
	
	
}
