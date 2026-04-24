package com.smartcampus.exception;

public class ErrorMessage {
    private int statusCode;
    private String message;
    private String errorType;

    public ErrorMessage() {}

    public ErrorMessage(int statusCode, String message, String errorType) {
        this.statusCode = statusCode;
        this.message = message;
        this.errorType = errorType;
    }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }
}
