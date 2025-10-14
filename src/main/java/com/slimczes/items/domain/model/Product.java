package com.slimczes.items.domain.model;

import com.slimczes.items.domain.exception.DomainException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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

    public void updateStock(int newQuantity) {
        validateQuantity(newQuantity);
        this.availableQuantity = newQuantity;
        this.updatedAt = Instant.now();
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
        this.availableQuantity += quantity;
        this.updatedAt = Instant.now();
    }

    public void removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to remove must be positive");
        }

        if (this.availableQuantity < quantity) {
            throw new DomainException("Insufficient stock. Available: " + availableQuantity +
                ", requested: " + quantity);
        }

        this.availableQuantity -= quantity;
        this.updatedAt = Instant.now();
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

    public void confirmReservationForOrder(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (reservedQuantity < quantity) {
            throw new DomainException("Cannot confirm more than reserved. Reserved: " + reservedQuantity);
        }

        this.reservedQuantity -= quantity;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (reservedQuantity > 0) {
            throw new DomainException("Cannot deactivate product with pending reservations");
        }

        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    // Query methods
    public boolean isAvailable() {
        return active && availableQuantity > 0;
    }

    public boolean canReserve(int quantity) {
        return active && availableQuantity >= quantity;
    }

    public int getTotalQuantity() {
        return availableQuantity + reservedQuantity;
    }

    // Validation methods
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

    // Getters
    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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
