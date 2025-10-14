package com.slimczes.items.api.proxy;

import com.slimczes.items.api.proxy.dto.CreateCustomerDto;
import com.slimczes.items.api.proxy.dto.CreatedCustomerDto;
import com.slimczes.items.api.proxy.dto.CustomerResponse;
import com.slimczes.items.api.proxy.exception.CustomerException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class CreateCustomerService {

    private final CustomerServiceClient customerServiceClient;
    private final CustomerMapper customerMapper;

    public CreatedCustomerDto createCustomer(CreateCustomerDto createCustomerDto) {
        try {
            CustomerResponse customerResponse = customerServiceClient.createCustomer(customerMapper.toCustomerRequest(createCustomerDto));
            return customerMapper.toCreatedCustomer(customerResponse);
        } catch (RetryableException ex) {
            log.error("Customer service is unreachable: {}", ex.getMessage());
            throw new CustomerException("Customer service is currently down", 503);
        }
    }

}
