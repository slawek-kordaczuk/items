package com.slimczes.items.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private final long amountInMinorUnits; // grosze dla PLN
    private final String currency;

    public Money(long amountInMinorUnits, String currency) {
        this.amountInMinorUnits = amountInMinorUnits;
        this.currency = currency;
    }

    public static Money zero(String currency) {
        return new Money(0, currency);
    }

    public static Money fromMajorUnits(BigDecimal amount, String currency) {
        long minorUnits = amount.multiply(BigDecimal.valueOf(100))
                                .setScale(0, RoundingMode.HALF_UP)
                                .longValue();
        return new Money(minorUnits, currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amountInMinorUnits + other.amountInMinorUnits, currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amountInMinorUnits - other.amountInMinorUnits, currency);
    }

    public Money multiply(int multiplier) {
        return new Money(this.amountInMinorUnits * multiplier, currency);
    }

    public boolean isNegative() {
        return amountInMinorUnits < 0;
    }

    public boolean isZero() {
        return amountInMinorUnits == 0;
    }

    public BigDecimal toMajorUnits() {
        return BigDecimal.valueOf(amountInMinorUnits)
                         .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private void validateSameCurrency(Money other) {
        if (!Objects.equals(this.currency, other.currency)) {
            throw new IllegalArgumentException("Cannot operate on different currencies");
        }
    }

    public long getAmountInMinorUnits() { return amountInMinorUnits; }
    public String getCurrency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amountInMinorUnits == money.amountInMinorUnits &&
            Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountInMinorUnits, currency);
    }

    @Override
    public String toString() {
        return toMajorUnits() + " " + currency;
    }
}
