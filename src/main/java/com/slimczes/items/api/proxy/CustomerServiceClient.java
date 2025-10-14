package com.slimczes.items.api.proxy;

import com.slimczes.items.api.proxy.config.FeignConfig;
import com.slimczes.items.api.proxy.dto.CreateCustomerRequest;
import com.slimczes.items.api.proxy.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "customer-service", url = "http://localhost:8080/customer",
        configuration = FeignConfig.class)
interface CustomerServiceClient {

    @PostMapping("/create")
    CustomerResponse createCustomer(CreateCustomerRequest request);

}
