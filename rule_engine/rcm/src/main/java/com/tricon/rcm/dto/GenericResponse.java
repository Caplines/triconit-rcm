package com.tricon.rcm.dto;

import org.springframework.http.HttpStatus;

/**
 * @author Deepak.Dogra
 *
 */
public class GenericResponse {

	String message;
	Object data;
	int status;

	public GenericResponse(HttpStatus status, String message, Object data) {
		super();
		this.status = status.value();
		this.message = message;
		this.data = data;

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
