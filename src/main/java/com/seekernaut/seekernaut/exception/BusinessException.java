package com.seekernaut.seekernaut.exception;

import java.util.Collections;
import java.util.List;

public class BusinessException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<String> messages;

    public BusinessException(String message) {
        super(message);
        this.messages = Collections.singletonList(message);
    }

    public BusinessException(List<String> messages) {
        super(messages.toString());
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String getMessage() {
        return String.join(", ", messages);
    }


}