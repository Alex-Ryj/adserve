package com.arit.adserve.comm;

public enum ErrorCodes {
    NOT_FOUND( -1, "not found");

    private final int code;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
