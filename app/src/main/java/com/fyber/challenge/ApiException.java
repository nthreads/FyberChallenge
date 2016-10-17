package com.fyber.challenge;
/**
 * Created by Nauman Zubair on 13/10/2016.
 */
public class ApiException extends Exception {

	public ApiException() {}
	
	public ApiException(String message) {
		super(message);
	}
}
