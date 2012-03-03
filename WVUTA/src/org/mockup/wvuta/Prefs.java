package org.mockup.wvuta;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Prefs extends PreferenceActivity{
	
	public static final String NOTIFICATIONS = "notifications";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
}
