package com.mathisonian.whisprabbit;

public class Response {
	private String r_id;
	private String content;
	
	public Response(String r, String c) {
		r_id = r;
		content = c;
	}

	public String getR_id() {
		return r_id;
	}

	public void setR_id(String r_id) {
		this.r_id = r_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
