package org.mockup.wvuta;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

public class ResetService extends Service{
	private static final String TAG = "WVUTA::RESETSERVICE";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "ResetService onCreate");
		
		SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
				Context.MODE_PRIVATE);
		
		Editor ed = prefs.edit();
		
		ed.putString(Constants.BEECHURST, "Up");
		ed.putString(Constants.ENGINEERING, "Up");
		ed.putString(Constants.MEDICAL, "Up");
		ed.putString(Constants.TOWERS, "Up");
		ed.putString(Constants.WALNUT, "Up");
		
		ed.putString("bsource", "WVUDOT");
		ed.putString("esource", "WVUDOT");
		ed.putString("msource", "WVUDOT");
		ed.putString("tsource", "WVUDOT");
		ed.putString("wsource", "WVUDOT");
		
		Calendar cal = Calendar.getInstance();
		String time = Constants.TWEETFORMAT.format(cal.getTime());
		
		ed.putString("btime", time);
		ed.putString("btime", time);
		ed.putString("btime", time);
		ed.putString("btime", time);
		ed.putString("btime", time);
		
		Log.d(TAG, "Finished reset.");
		
		stopSelf();
	}
}
