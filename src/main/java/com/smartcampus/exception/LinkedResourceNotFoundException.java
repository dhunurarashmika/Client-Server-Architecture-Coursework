package com.smartcampus.exception;

/**
 * Custom exception thrown when a resource relies on a parent resource that does not exist.
 * (e.g., Creating a Sensor in a Room that does not exist).
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}