package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentMethod {
    private final String id;
    private final int discountPercentage;
    private long limitInCents;

    @JsonCreator
    public PaymentMethod(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "discount", required = true) String discount,
            @JsonProperty(value = "limit", required = true) String limit) {
        this.id = id;
        this.discountPercentage = Integer.parseInt(discount);
        this.limitInCents = Math.round(Double.parseDouble(limit.replace(",", ".")) * 100);
    }

    public String getId() {
        return id;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public long getLimitInCents() {
        return limitInCents;
    }

    public void decreaseLimit(long amount) {
        if (amount > limitInCents) {
            throw new IllegalArgumentException("No sufficient limit " + id);
        }
        this.limitInCents -= amount;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id='" + id + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", limitInCents=" + limitInCents +
                '}';
    }
}
