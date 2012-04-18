package org.mockup.wvuta;

public class BusTweet {

	private String tweet;
	private String time;
	
	public BusTweet(String tweet, String time){
		this.tweet = tweet;
		this.time = time;
	}

	public String getTweet() {
		return tweet;
	}

	public String getTime() {
		return time;
	}	
}
