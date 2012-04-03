package org.mockup.wvuta;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
	private ArrayList<String> times = new ArrayList<String>();
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
		if (!times.isEmpty()) {
			updateLatest();
		}
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
			String b_time = null, e_time = null, m_time = null;
			String t_time = null, w_time = null;

			Iterator<String> report_it = reportArray.iterator();
			Iterator<String> time_it = times.iterator();
			SharedPreferences prefs = getSharedPreferences(Constants.LATEST,
					Context.MODE_PRIVATE);
			while (report_it.hasNext()) {
				String status = report_it.next();
				report_it.next();
				String location = report_it.next();
				String source = report_it.next();
				String time = time_it.next();
				if (beech_status == null
						&& location.equalsIgnoreCase("beechurst")) {
					beech_status = status;
					b_source = source;
					b_time = time;
				}
				if (eng_status == null
						&& location.equalsIgnoreCase("engineering")) {
					eng_status = status;
					e_source = source;
					e_time = time;
				}
				if (med_status == null && location.equalsIgnoreCase("medical")) {
					med_status = status;
					m_source = source;
					m_time = time;
				}
				if (tow_status == null && location.equalsIgnoreCase("towers")) {
					tow_status = status;
					t_source = source;
					t_time = time;
				}
				if (wal_status == null && location.equalsIgnoreCase("walnut")) {
					wal_status = status;
					w_source = source;
					w_time = time;
				}
			}

			Date b_date = null, e_date = null, m_date = null, t_date = null, w_date = null;
			try {
				b_date = b_time == null ? null : Constants.TWEETFORMAT
						.parse(b_time);
				e_date = e_time == null ? null : Constants.TWEETFORMAT
						.parse(e_time);
				m_date = m_time == null ? null : Constants.TWEETFORMAT
						.parse(m_time);
				t_date = t_time == null ? null : Constants.TWEETFORMAT
						.parse(t_time);
				w_date = w_time == null ? null : Constants.TWEETFORMAT
						.parse(w_time);
			} catch (ParseException e) {
				Log.d(TAG, "Couldn't parse dates. top");
			}

			Date beech = null, eng = null, tow = null, med = null, wal = null;
			try {
				beech = Constants.TWEETFORMAT.parse(prefs.getString("btime",
						null));
				eng = Constants.TWEETFORMAT.parse(prefs
						.getString("etime", null));
				med = Constants.TWEETFORMAT.parse(prefs
						.getString("mtime", null));
				tow = Constants.TWEETFORMAT.parse(prefs
						.getString("ttime", null));
				wal = Constants.TWEETFORMAT.parse(prefs
						.getString("wtime", null));
			} catch (Exception e) {
				Log.d(TAG, "Couldn't parse dates. bottom");
			}

			Editor ed = prefs.edit();
			if (b_date != null && beech != null && b_date.after(beech)) {
				if (beech_status != null) {
					ed.putString(Constants.BEECHURST, beech_status);
					ed.putString("bsource", b_source);
					ed.putString("btime", b_time);
				}
			}
			if (e_date != null && eng != null && e_date.after(eng)) {
				if (eng_status != null) {
					ed.putString(Constants.ENGINEERING, eng_status);
					ed.putString("esource", e_source);
					ed.putString("etime", e_time);
				}
			}
			if (m_date != null && med != null && m_date.after(med)) {
				if (med_status != null) {
					ed.putString(Constants.MEDICAL, med_status);
					ed.putString("msource", m_source);
					ed.putString("mtime", m_time);
				}
			}
			if (t_date != null && tow != null && t_date.after(tow)) {
				if (tow_status != null) {
					ed.putString(Constants.TOWERS, tow_status);
					ed.putString("tsource", t_source);
					ed.putString("ttime", t_time);
				}
			}
			if (w_date != null && wal != null && w_date.after(wal)) {
				if (wal_status != null) {
					ed.putString(Constants.WALNUT, wal_status);
					ed.putString("wsource", w_source);
					ed.putString("wtime", w_time);
				}
			}
			Log.d(TAG, "added prefs - reports");
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
					// df1.setTimeZone(TimeZone.getTimeZone("est"));
					Date date1 = df1.parse(jobj.getString("time"));
					date1.setHours(date1.getHours() + 3);
					// Log.d(TAG, "original: " + jobj.getString("time"));
					// Log.d(TAG, "df1: " + date1.toString());
					// Log.d(TAG, "tweet: " +
					// Constants.TWEETFORMAT.format(date1));

					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					Date date = df.parse(time);
					date.setHours(date.getHours() + 3);
					String newTime = DateFormat.getTimeInstance(
							DateFormat.SHORT).format(date);

					Calendar cal = Calendar.getInstance();
					if (cal.get(Calendar.DATE) == date1.getDate()
							&& cal.get(Calendar.MONTH) == date1.getMonth()) {
						reportArray.add(status);
						reportArray.add(newTime);
						reportArray.add(location);
						reportArray.add(source);
						times.add(Constants.TWEETFORMAT.format(date1));
					}

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
