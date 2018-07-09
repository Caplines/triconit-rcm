package com.tricon.ruleengine.api.controller;

public class AuthenticationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8200966675316277416L;

	public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
