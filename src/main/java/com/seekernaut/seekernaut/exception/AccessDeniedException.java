package com.seekernaut.seekernaut.exception;

public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 406940878937645084L;

	public AccessDeniedException() {
		super();
	}

	public AccessDeniedException(String message) {
		super(message);
	}


}
