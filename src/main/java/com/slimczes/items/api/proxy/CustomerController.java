package com.slimczes.items.api.proxy;

import com.slimczes.items.api.proxy.dto.CreateCustomerResponse;
import com.slimczes.items.api.proxy.dto.CreatedCustomerDto;
import com.slimczes.items.api.proxy.dto.CustomerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

    private final CreateCustomerService createCustomerService;
    private final CustomerMapper customerMapper;

    @PostMapping("/create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CustomerRequest createRequest) {
        log.info("CustomerController.createCustomer");
        CreatedCustomerDto createdCustomerDto = createCustomerService.createCustomer(customerMapper.toCustomerDto(createRequest));
        return ResponseEntity.status(201).body(new CreateCustomerResponse(createdCustomerDto.id()));
    }
}
