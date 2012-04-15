package org.mockup.wvuta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class BusWebStatus extends Activity implements OnClickListener{
	private static final String TAG = "WVUTA::BUSWEBSTATUS";
	private String busStatusUrl = "http://site.busride.org/mybus/mybus.gif";
	private String busmap = "http://site.busride.org/mybus/mybus.gif";
	private WebView webview;
	private int xOffset = 0;
	private int yOffset = 0;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busweb);
		
		Log.d(TAG, "BusWebStatus onCreate");
		
		Button tableButton = (Button) findViewById(R.id.BusTableButton);
		Button mapButton = (Button) findViewById(R.id.BusMapButton);
		
		tableButton.setOnClickListener(this);
		mapButton.setOnClickListener(this);
		
		webview = (WebView) findViewById(R.id.busWebView);
		openBrowser();
	}

	private void openBrowser() {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLoadsImagesAutomatically(true);
		webview.setClickable(true);
		webview.loadUrl(busStatusUrl);
		//webview.scrollTo(xOffset, yOffset);
	}

	public void onClick(View v) {
		switch (v.getId()){
		case R.id.BusTableButton:
			Log.d(TAG, "Scroll to Table");
			xOffset = 0;
			yOffset = 0;
			break;
		case R.id.BusMapButton:
			Log.d(TAG, "Scroll to Map");
			xOffset = 1000;
			yOffset = 125;
			break;
		}
		webview.scrollTo(xOffset, yOffset);
	}
}
