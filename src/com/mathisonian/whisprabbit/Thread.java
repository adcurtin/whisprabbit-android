package com.mathisonian.whisprabbit;

public class Thread {
	private String t_id;
	private String content;
	
	public Thread(String t, String c) {
		t_id = t;
		content = c;
	}
	
	// Generated setters and getters
	// we probably only want getters
	public String getT_id() {
		return t_id;
	}
	public void setT_id(String t_id) {
		this.t_id = t_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
