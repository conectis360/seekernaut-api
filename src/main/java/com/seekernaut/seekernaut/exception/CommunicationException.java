package com.seekernaut.seekernaut.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

public class CommunicationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int statusCode;
    private List<String> messages;

    public CommunicationException(String message) {
        super(message);
        this.messages = Collections.singletonList(message);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public CommunicationException(List<String> messages) {
        super(messages.toString());
        this.messages = messages;
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public CommunicationException(int statusCode, String message) {
        super(message);
        this.messages = Collections.singletonList(message);
        this.statusCode = statusCode;
    }

    public CommunicationException(int statusCode, List<String> messages) {
        super(messages.toString());
        this.messages = messages;
        this.statusCode = statusCode;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String getMessage() {
        return String.join(", ", messages);
    }

    public int getStatusCode() {
        return statusCode;
    }
}