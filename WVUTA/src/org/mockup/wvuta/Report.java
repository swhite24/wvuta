package org.mockup.wvuta;

// Simple class to better organize relevant reporting info

public class Report {
	
	private String location, time, status, note;
	
	public Report(String location, String time, String status){
		this.location = location;
		this.time = time;
		this.status = status;
		this.note = "User Report";
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
		this.note = note;
	}
	
	public String getNote(){
		return note;
	}
}
