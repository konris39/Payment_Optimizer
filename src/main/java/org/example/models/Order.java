package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;

public class Order {
    private final String id;
    private final long valueInCents;
    private final Set<String> promotions;

    @JsonCreator
    public Order(
        @JsonProperty(value = "id", required = true) String id,
        @JsonProperty(value = "value", required = true) String value,
        @JsonProperty("promotions") Set<String> promotions
    ) {
        this.id = id;
        this.valueInCents = Math.round(Double.parseDouble(value) * 100);
        this.promotions = promotions != null
            ? Collections.unmodifiableSet(promotions)
            : Collections.emptySet();
    }

    public String getId() {
        return id;
    }

    public long getValueInCents() {
        return valueInCents;
    }

    public Set<String> getPromotions() {
        return promotions;
    }

    @Override
    public String toString() {
        return "Order{" +
               "id='" + id + '\'' +
               ", valueInCents=" + valueInCents +
               ", promotions=" + promotions +
               '}';
    }
}
