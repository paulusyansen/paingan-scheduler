package org.paingan.scheduler.util;

public class ResponseBody {
	
	private static final String SUCCESS = "success";
	private static final String ERROR =  "error";
	
	private String message;
	private String status;
	private Object data;
	
	private ResponseBody() {}
	
	private ResponseBody(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	private ResponseBody(String status, String message, Object data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	public static ResponseBody success(String message) {
		return new ResponseBody(SUCCESS, message);
	}
	
	public static ResponseBody success(String message, Object data) {
		return new ResponseBody(SUCCESS, message, data);
	}
	
	public static ResponseBody error(String message) {
		return new ResponseBody(ERROR, message);
	}
	
	public static ResponseBody error(String message, Object data) {
		return new ResponseBody(ERROR, message, data);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
