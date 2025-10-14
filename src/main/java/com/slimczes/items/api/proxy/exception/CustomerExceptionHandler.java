package com.slimczes.items.api.proxy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomerExceptionHandler {

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<ErrorResponse> handleCustomerException(CustomerException ex) {
        log.error("Customer service error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse("CUSTOMER_ERROR", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {
    }
}
