package org.mockup.wvuta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class BusMap extends Activity {

	private static final String TAG = "WVUTA::BUSMAP";
	private static final String MAP_URL = "http://site.busride.org/mybus/mybus.gif";
	private WebView map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busmap);

		Log.d(TAG, "BusMap onCreate");

		map = (WebView) findViewById(R.id.busmap_webview);
		openBrowser();
	}

	/**
	 * Initializes WebView and loads URL
	 */
	private void openBrowser() {
		map.getSettings().setJavaScriptEnabled(true);
		map.getSettings().setBuiltInZoomControls(true);
		map.getSettings().setLoadsImagesAutomatically(true);
		map.setClickable(true);
		map.loadUrl(MAP_URL);
		//webview.scrollTo(xOffset, yOffset);
	}

}
