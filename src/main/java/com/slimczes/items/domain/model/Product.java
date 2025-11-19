package com.slimczes.items.domain.model;

import com.slimczes.items.domain.exception.DomainException;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Product {

    private final UUID id;
    private final String sku;
    private final String name;
    private int availableQuantity;
    private int reservedQuantity;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Product(String sku, String name, int availableQuantity) {
        this.id = null;
        this.sku = validateSku(sku);
        this.name = validateName(name);
        this.availableQuantity = validateQuantity(availableQuantity);
        this.reservedQuantity = 0;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Default
    public Product(UUID id, String sku, String name, int availableQuantity,
            int reservedQuantity, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ReservationResult reserveForOrder(int quantity) {
        if (quantity <= 0) {
            return ReservationResult.failure(ReservationStatus.INVAlID_QUANTITY);
        }

        if (!active) {
            return ReservationResult.failure(ReservationStatus.NOT_ACTIVE);
        }

        if (availableQuantity < quantity) {
            return ReservationResult.failure(ReservationStatus.NOT_AVAILABLE);
        }

        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        this.updatedAt = Instant.now();

        return ReservationResult.success(quantity);
    }

    public void cancelReserveForOrder(int quantity) {
        this.availableQuantity += quantity;
        this.reservedQuantity -= quantity;
        this.updatedAt = Instant.now();
    }


    private String validateSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (sku.length() > 64) {
            throw new IllegalArgumentException("SKU too long (max 64 characters)");
        }
        return sku.trim().toUpperCase();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Name too long (max 255 characters)");
        }
        return name.trim();
    }

    private int validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product product)) {
            return false;
        }
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
