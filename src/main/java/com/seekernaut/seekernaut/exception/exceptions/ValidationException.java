package com.seekernaut.seekernaut.exception.exceptions;

import java.util.Arrays;
import java.util.List;

public class ValidationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<String> messages;

    public ValidationException(String message) {
        super(message);
        this.messages = Arrays.asList(message);
    }

    public ValidationException(List<String> messages) {
        super(messages.toString());
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

}
