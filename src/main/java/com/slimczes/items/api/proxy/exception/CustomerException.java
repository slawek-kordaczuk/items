package com.slimczes.items.api.proxy.exception;

import lombok.Getter;

@Getter
public class CustomerException extends RuntimeException {
    private final int status;

    public CustomerException(String message, int status) {
        super(message);
        this.status = status;
    }
}
