package org.mockup.wvuta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

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
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class RetrievingService extends Service {

	private ReportLookupTask task = null;
	public static final String REPORTS = "org.mockup.wvuta.REPORTS";
	private static final String TAG = "WVUTA::RetrievingService";
	private final ArrayList<String> reportArray = new ArrayList<String>();
	private ArrayList<Report> reports = new ArrayList<Report>();
	private boolean error = false;

	@Override
	public IBinder onBind(Intent intent) {
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

	/**
	 * Starts a new ReportLookupTask with provided filters.
	 */
	private void getDBInfo() {
		// create ASyncTask to put server communication in background
		if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
			task = new ReportLookupTask();
			task.execute((Void[]) null);
		}
	}

	/**
	 * Broadcasts results of latest reports.
	 */
	private void announceResults() {
		// broadcast results
		Log.d(TAG, "Broadcasting report retrieval results");
		Intent intent;
		intent = new Intent(REPORTS);
		intent.putStringArrayListExtra("strings", reportArray);
		updateLatest();
		sendBroadcast(intent);
		stopSelf();
	}

	/**
	 * Updates preferences with latest status and source for each station.
	 */
	private void updateLatest() {
		if (!error) {
			String beech_status = null;
			String eng_status = null;
			String med_status = null;
			String tow_status = null;
			String wal_status = null;
			String b_source = null, e_source = null, m_source = null;
			String t_source = null, w_source = null;

			Iterator<String> it = reportArray.iterator();

			SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
					Context.MODE_PRIVATE);
			while (it.hasNext()) {
				String status = it.next();
				it.next();
				String location = it.next();
				String source = it.next();

				if (beech_status == null
						&& location.equalsIgnoreCase("beechurst")) {
					beech_status = status;
					b_source = source;
				}
				if (eng_status == null
						&& location.equalsIgnoreCase("engineering")) {
					eng_status = status;
					e_source = source;
				}
				if (med_status == null && location.equalsIgnoreCase("medical")) {
					med_status = status;
					m_source = source;
				}
				if (tow_status == null && location.equalsIgnoreCase("towers")) {
					tow_status = status;
					t_source = source;
				}
				if (wal_status == null && location.equalsIgnoreCase("walnut")) {
					wal_status = status;
					w_source = source;
				}
			}

			Editor ed = prefs.edit();
			if (!prefs.getString(Constants.BEECHURST, null)
					.equals(beech_status)) {
				ed.putString(Constants.BEECHURST, beech_status);
				ed.putString("bsource", b_source);
			}
			if (!prefs.getString(Constants.ENGINEERING, null)
					.equals(eng_status)) {
				ed.putString(Constants.ENGINEERING, eng_status);
				ed.putString("esource", e_source);
			}
			if (!prefs.getString(Constants.MEDICAL, null).equals(med_status)) {
				ed.putString(Constants.MEDICAL, med_status);
				ed.putString("msource", m_source);
			}
			if (!prefs.getString(Constants.TOWERS, null).equals(tow_status)) {
				ed.putString(Constants.TOWERS, tow_status);
				ed.putString("tsource", t_source);
			}
			if (!prefs.getString(Constants.WALNUT, null).equals(wal_status)) {
				ed.putString(Constants.WALNUT, wal_status);
				ed.putString("wsource", w_source);
			}

			ed.commit();
		}
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
					String source = jobj.getString("source");
					String location = jobj.getString("location");
					String status = jobj.getString("status");
					int space = jobj.getString("time").indexOf(" ");
					String time = jobj.getString("time").substring(space + 1);
					SimpleDateFormat df1 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					df1.setTimeZone(TimeZone.getTimeZone("est"));
					Date date1 = df1.parse(jobj.getString("time"));
					Log.d(TAG, "original: " + jobj.getString("time"));
					Log.d(TAG, "df1: " + date1.toString());

					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					Date date = df.parse(time);
					date.setHours(date.getHours() + 3);
					String newTime = DateFormat.getTimeInstance(
							DateFormat.SHORT).format(date);

					reports.add(new Report(location, newTime, status, source));

					reportArray.add(status);
					reportArray.add(newTime);
					reportArray.add(location);
					reportArray.add(source);

				}
			} catch (Exception e) {
				error = true;
				output.append("No results to display");
				reportArray.add(" ");
				reportArray.add(" ");
				reportArray.add("No results to display");
				reportArray.add(" ");
				Log.e(TAG, "failed to extract: " + e.toString());
			}

			result = output.toString();
			announceResults();
			return result;
		}
	};

}
