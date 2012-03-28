package org.mockup.wvuta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import winterwell.jtwitter.Twitter;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class TweetService extends Service {

	private static final String TAG = "WVUTA::TWEETSERVICE";
	private TweetLookupTask tweetTask;
	private ArrayList<Report> reports = new ArrayList<Report>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "TweetService onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		tweetTask = new TweetLookupTask();
		tweetTask.execute((Void[]) null);
		return super.onStartCommand(intent, flags, startId);
	}

	private void compile_tweets(List<winterwell.jtwitter.Status> tweets) {
		String beech = null;
		String walnut = null;
		String eng = null;
		String tow = null;
		String med = null;
		Date time = null;
		Pattern prt_pattern = Pattern.compile("[pP][rR][tT].*");

		Pattern beech_pattern = Pattern.compile("BEECH.*|BEE.*");
		Pattern walnut_pattern = Pattern.compile("WAL.*");
		Pattern eng_pattern = Pattern.compile("ENG.*");
		Pattern tow_pattern = Pattern.compile("TOW.*");
		Pattern med_pattern = Pattern.compile("MED.*");

		Pattern time_pattern = Pattern.compile("\\(.*\\)");

		Pattern down_pattern = Pattern.compile("DOWN.*|OUT OF SERVICE.*");
		Pattern all_down_pattern = Pattern.compile("closed.*");
		Pattern up_pattern = Pattern.compile("RUNNING.*NORM");

		for (int i = 0; i < tweets.size(); i++) {
			beech = null;
			walnut = null;
			eng = null;
			tow = null;
			med = null;
			time = null;
			Matcher prt_matcher = prt_pattern.matcher(tweets.get(i).toString());
			if (prt_matcher.find()) {
				String prt_tweet = prt_matcher.group().toUpperCase();
				int bus_index = -1;
				String prt_no_bus = prt_tweet;
				if ((bus_index = prt_tweet.indexOf("BUS")) != -1) {
					prt_no_bus = prt_tweet.substring(0, bus_index);
				}
				Matcher up_matcher = up_pattern.matcher(prt_no_bus);
				Matcher down_matcher = down_pattern.matcher(prt_no_bus);
				Matcher all_down_matcher = all_down_pattern.matcher(prt_no_bus);
				Matcher time_matcher = time_pattern.matcher(prt_tweet);
				if (time_matcher.find()) {
					SimpleDateFormat df = new SimpleDateFormat(
							"(MMM. dd @ hh:mmaa)");
					try {
						time = df.parse(time_matcher.group());
					} catch (ParseException e) {
						Log.d(TAG, "Invalid tweet time format.");
					}
				}
				if (up_matcher.find()) {
					beech = "up";
					walnut = "up";
					eng = "up";
					tow = "up";
					med = "up";
				} else if (all_down_matcher.find()) {
					beech = "down";
					walnut = "down";
					eng = "down";
					tow = "down";
					med = "down";
				} else if (down_matcher.find()) {
					Matcher beech_matcher = beech_pattern.matcher(down_matcher
							.group());
					Matcher wal_matcher = walnut_pattern.matcher(down_matcher
							.group());
					Matcher eng_matcher = eng_pattern.matcher(down_matcher
							.group());
					Matcher tow_matcher = tow_pattern.matcher(down_matcher
							.group());
					Matcher med_matcher = med_pattern.matcher(down_matcher
							.group());
					if (beech_matcher.find()) {
						beech = "down";
					}
					if (wal_matcher.find()) {
						walnut = "down";
					}
					if (eng_matcher.find()) {
						eng = "down";
					}
					if (tow_matcher.find()) {
						tow = "down";
					}
					if (med_matcher.find()) {
						med = "down";
					}

				}
			}
			if (time != null) {
				if (beech != null) {
					reports.add(new Report("Beechurst", time.toString(), beech,
							"wvudot"));
				}
				if (walnut != null) {
					reports.add(new Report("Walnut", time.toString(), walnut,
							"wvudot"));
				}
				if (eng != null) {
					reports.add(new Report("Engineering", time.toString(), eng,
							"wvudot"));
				}
				if (tow != null) {
					reports.add(new Report("Towers", time.toString(), tow,
							"wvudot"));
				}
				if (med != null) {
					reports.add(new Report("Medical", time.toString(), med,
							"wvudot"));
				}
			}
		}
	}

	private class TweetLookupTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Twitter mtwitter = new Twitter();
			mtwitter.setMaxResults(1);
			List<winterwell.jtwitter.Status> tweets = mtwitter
					.getUserTimeline("WVUDOT");

			compile_tweets(tweets);
			return null;
		}

	}

}
