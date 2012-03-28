package org.mockup.wvuta;

// Simple class to better organize relevant reporting info

public class Report {
	
	private String location, time, status, source;
	
	public Report(String location, String time, String status, String source){
		this.location = location;
		this.time = time;
		this.status = status;
		this.source = source;
	}

	public String getLocation() {
		return location;
	}

	public String getTime() {
		return time;
	}

	public String getStatus() {
		return status;
	}
	
	public void setNote(String note){
		this.source = note;
	}
	
	public String getNote(){
		return source;
	}
}
