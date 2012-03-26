package org.mockup.wvuta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class RetrievingService extends Service {

	private ReportLookupTask task = null;
	public static final String REPORTS = "org.mockup.wvuta.REPORTS";
	private static final String TAG = "WVUTA::RetrievingService";
	private final ArrayList<String> reportArray = new ArrayList<String>();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "RetrievingService onCreate");
		getDBInfo();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		getDBInfo();
		return Service.START_STICKY;

	}

	private void getDBInfo() {
		// create ASyncTask to put server communication in background
		if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
			task = new ReportLookupTask();
			task.execute((Void[]) null);
		}
	}

	private void announceResults() {
		// broadcast results
		Log.d(TAG, "Broadcasting report retrieval results");
		Intent intent = new Intent(REPORTS);
		intent.putStringArrayListExtra("strings", reportArray);
		sendBroadcast(intent);
		stopSelf();
	}

	private class ReportLookupTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			InputStream inStream = null;
			StringBuilder builder = new StringBuilder();
			StringBuilder output = new StringBuilder();
			String result = "";

			SharedPreferences prefs = getSharedPreferences(Constants.TABLENAME,
					Context.MODE_PRIVATE);
			
			// put filters, null or not, into ArrayList to send to server
			ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
			al.add(new BasicNameValuePair("location", prefs.getString(
					"location", null)));
			al.add(new BasicNameValuePair("status", prefs.getString("status",
					null)));
			Log.d(TAG,
					prefs.getString("location", "x") + " "
							+ prefs.getString("status", "y"));

			// connect
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://www.prtstatus.com/retrieveFilter.php");
				httppost.setEntity(new UrlEncodedFormEntity(al));
				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity entity = httpresponse.getEntity();
				inStream = entity.getContent();
			} catch (Exception e) {
				Log.e(TAG, "failed to connect: " + e.toString());
			}

			// read response
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream), 15);
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}
				inStream.close();
			} catch (Exception e) {
				Log.e(TAG, "failed to read: " + e.toString());
			}
			output.append(String.format("%-15.15s%-15.15s\n", "Status:",
					"Location:"));
			result = builder.toString();
			// put results into usable format
			reportArray.clear();
			try {
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jobj = jArray.getJSONObject(i);
					String location = jobj.getString("location");
					String status = jobj.getString("status");
					int space = jobj.getString("time").indexOf(" ");
					String time = jobj.getString("time").substring(space + 1);

					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					Date date = df.parse(time);
					date.setHours(date.getHours()+3);
					String newTime = DateFormat.getTimeInstance(
							DateFormat.SHORT).format(date);

					reportArray.add(status);
					reportArray.add(newTime);
					reportArray.add(location);
				}
			} catch (Exception e) {
				output.append("No results to display");
				reportArray.add(" ");
				reportArray.add(" ");
				reportArray.add("No results to display");
				Log.e(TAG, "failed to extract: " + e.toString());
			}

			result = output.toString();
			announceResults();
			return result;
		}
	};

}
