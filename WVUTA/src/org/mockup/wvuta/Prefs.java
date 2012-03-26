package org.mockup.wvuta;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Prefs extends PreferenceActivity{
	
	public static final String NOTIFICATIONS = "notifications";
	private static final String TAG = "WVUTA::PREFS";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Log.d(TAG, "Prefs onCreate");
	}
	
}
