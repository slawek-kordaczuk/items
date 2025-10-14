package com.slimczes.items.api.proxy;

import com.slimczes.items.api.proxy.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    CreateCustomerDto toCustomerDto(CustomerRequest customerRequest);
    CreateCustomerRequest toCustomerRequest(CreateCustomerDto createCustomerDto);
    @Mapping(target = "id", source = "customerId")
    CreatedCustomerDto toCreatedCustomer(CustomerResponse createCustomerResponse);
}
