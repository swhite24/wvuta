package org.mockup.wvuta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class About extends Activity{
	private static final String TAG = "WVUTA::ABOUT";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		Log.d(TAG, "About onCreate");
	}

} 
