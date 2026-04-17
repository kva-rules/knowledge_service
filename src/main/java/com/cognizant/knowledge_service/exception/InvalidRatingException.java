package com.cognizant.knowledge_service.exception;

public class InvalidRatingException extends RuntimeException {

    public InvalidRatingException(String message) {
        super(message);
    }

    public InvalidRatingException(String message, Throwable cause) {
        super(message, cause);
    }
}
