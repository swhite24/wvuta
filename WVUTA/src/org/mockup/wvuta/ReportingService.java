package org.mockup.wvuta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class ReportingService extends Service {

	private SendReportTask task = null;
	public static final String REPORTING = "org.mockup.wvuta.REPORTING";
	private static final String TAG = "WVUTA::REPORTINGSERVICE";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "ReportingService onCreate");
		sendReport();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	private void sendReport(){
		// Put server communication in background thread
		if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)){
			task = new SendReportTask();
			task.execute((Void[])null);
		}
	}
	
	private void announceResult(String result){
		// Broadcast response from server
		Log.d(TAG, "Broadcasting report result");
		Intent intent = new Intent(REPORTING);
		intent.putExtra("response", result);
		sendBroadcast(intent);
		stopSelf();
	}

	private class SendReportTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			InputStream inStream = null;
			StringBuilder builder = new StringBuilder();
			SharedPreferences prefs = getSharedPreferences(Constants.TABLENAME,
					Context.MODE_PRIVATE);

			// add relevant values to ArrayList which will be sent to database
			ArrayList<NameValuePair> namevaluepair = new ArrayList<NameValuePair>();
			namevaluepair.add(new BasicNameValuePair("source", "User Report"));
			namevaluepair.add(new BasicNameValuePair("status", prefs.getString(
					Constants.STATUS, "unknown")));
			namevaluepair.add(new BasicNameValuePair("location", prefs.getString(
					Constants.LOCATION, "unknown")));

			// connect
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://www.prtstatus.com/updateReports.php");
				httppost.setEntity(new UrlEncodedFormEntity(namevaluepair));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				inStream = entity.getContent();
			} catch (Exception e) {
				Log.e(TAG, "Connection issue: " + e.toString());
			}

			// Read Response from server
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream), 10);
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				inStream.close();
			} catch (Exception e) {
				Log.e(TAG, "\nReading issue: " + e.toString());
			}
			String result = builder.toString();
			announceResult(result);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
		}

	}

}
