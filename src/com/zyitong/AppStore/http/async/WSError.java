package com.zyitong.AppStore.http.async;

public class WSError extends Throwable {

	private static final long serialVersionUID = 1L;

	private String message;

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}