package com.seekernaut.seekernaut.exception;

public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 406940878937645084L;

	public EntityNotFoundException() {
		super();
	}
	
	public EntityNotFoundException(String message) {
		super(message);
	}


}
