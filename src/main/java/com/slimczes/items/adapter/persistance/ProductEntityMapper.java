package com.slimczes.items.adapter.persistance;

import com.slimczes.items.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductEntityMapper {

    ProductEntity toEntity(Product product);

    Product toProduct(ProductEntity productEntity);
}
