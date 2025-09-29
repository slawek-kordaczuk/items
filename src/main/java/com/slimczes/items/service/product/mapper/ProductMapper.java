package com.slimczes.items.service.product.mapper;

import com.slimczes.items.adapter.api.CreateProductResponse;
import com.slimczes.items.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    CreateProductResponse responseFromProduct(Product product);

}
