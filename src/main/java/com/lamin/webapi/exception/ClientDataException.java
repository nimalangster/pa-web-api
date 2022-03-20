package com.lamin.webapi.exception;

public class ClientDataException extends RuntimeException {
    public ClientDataException(String message) {
    	super(message);
    }

	public ClientDataException(Throwable failure) {
		// TODO Auto-generated constructor stub
	}
}