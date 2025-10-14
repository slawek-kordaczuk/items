package com.slimczes.items.api.proxy.config;

import com.slimczes.items.api.proxy.exception.CustomerException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        log.error("Feign client error: method={}, status={}, reason={}",
                methodKey, response.status(), response.reason());

        return switch (status) {
            case BAD_REQUEST -> new CustomerException("Invalid customer data provided", status.value());
            case NOT_FOUND -> new CustomerException("Customer service endpoint not found", status.value());
            case INTERNAL_SERVER_ERROR -> new CustomerException("Customer service is temporarily unavailable", status.value());
            case SERVICE_UNAVAILABLE -> new CustomerException("Customer service is currently down", status.value());
            default -> new CustomerException("Customer service error: " + response.reason(), response.status());
        };

    }
}
