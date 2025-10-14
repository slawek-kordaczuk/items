package com.slimczes.items.service.product;

import com.slimczes.items.api.CreateProductResponse;
import com.slimczes.items.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ProductMapper {

    CreateProductResponse responseFromProduct(Product product);

}
