package com.tricon.rcm.exception;

public class AuthenticationException  extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4766358546148343859L;

	public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
