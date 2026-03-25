package com.tricon.rcm.exception;

/**
 * Thrown when a claim write operation fails due to business validation,
 * so the API can return a user-friendly error message.
 */
public class ClaimSaveException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClaimSaveException(String message) {
		super(message);
	}
}

