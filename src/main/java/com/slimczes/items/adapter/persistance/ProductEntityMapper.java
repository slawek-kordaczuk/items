package com.slimczes.items.adapter.persistance;

import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.model.ProductReservation;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ProductEntityMapper {

    @Mapping(target = "productReservations", source = "reservations")
    ProductEntity toEntity(Product product);

    @Mapping(target = "reservations", source = "productReservations")
    Product toProduct(ProductEntity productEntity);

    @Mapping(target = "productReservationStatus", source = "status")
    @Mapping(target = "product", ignore = true)
    ProductReservation toProductReservation(ProductReservationEntity productReservationEntity);

    @Mapping(target = "status", source = "productReservationStatus")
    @Mapping(target = "product", ignore = true)
    ProductReservationEntity toProductReservationEntity(ProductReservation productReservation);

    @AfterMapping
    default void linkProductReservations(@MappingTarget ProductEntity productEntity) {
        if (productEntity.getProductReservations() != null) {
            productEntity.getProductReservations()
                    .forEach(reservation -> reservation.setProduct(productEntity));
        }
    }

}
