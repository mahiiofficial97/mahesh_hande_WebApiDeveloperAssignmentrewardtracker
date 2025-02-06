package com.rewardtracker.model;

import lombok.Data;

@Data
public class JsonResponseClass {
	
	public JsonResponseClass() {
		super();
	}
	public JsonResponseClass(String status, String result, String message) {
		super();
		this.status = status;
		this.result = result;
		this.message = message;
	}
	private String status;
	private String result;
	private String message;
	

}
