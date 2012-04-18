package org.mockup.wvuta;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusTweetAdapter extends ArrayAdapter<BusTweet> {

	private int resource;

	public BusTweetAdapter(Context context, int textViewResourceId,
			List<BusTweet> objects) {
		super(context, textViewResourceId, objects);
		this.resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout rowView;

		BusTweet bTweet = getItem(position);

		String time = bTweet.getTime();
		String tweet = bTweet.getTweet();

		// Inflate resource layout
		if (convertView == null) {
			rowView = new LinearLayout(getContext());
			String inflate = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					inflate);
			li.inflate(resource, rowView, true);
		} else {
			rowView = (LinearLayout) convertView;
		}

		TextView tweetCol = (TextView) rowView.findViewById(R.id.tweetrow_tweet);
		TextView timeCol = (TextView) rowView.findViewById(R.id.tweetrow_time);

		// Set appropriate values
		tweetCol.setText(tweet);
		timeCol.setText(time);

		return rowView;
	}

}
