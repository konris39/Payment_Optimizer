package org.example.models;

public class AllocationType {
    private final String orderId;
    private final String paymentMethodId;
    private final long netPaymentInCents;
    private final long pointsUsedInCents;
    private final long cardUsedInCents;

    public AllocationType(String orderId,
                          String paymentMethodId,
                          long netPaymentInCents,
                          long pointsUsedInCents,
                          long cardUsedInCents) {
        this.orderId = orderId;
        this.paymentMethodId = paymentMethodId;
        this.netPaymentInCents = netPaymentInCents;
        this.pointsUsedInCents = pointsUsedInCents;
        this.cardUsedInCents = cardUsedInCents;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public long getNetPaymentInCents() {
        return netPaymentInCents;
    }

    public long getPointsUsedInCents() {
        return pointsUsedInCents;
    }

    public long getCardUsedInCents() {
        return cardUsedInCents;
    }

    @Override
    public String toString() {
        return "AllocationType{" +
               "orderId='" + orderId + '\'' +
               ", paymentMethodId='" + paymentMethodId + '\'' +
               ", netPaymentInCents=" + netPaymentInCents +
               ", pointsUsedInCents=" + pointsUsedInCents +
               ", cardUsedInCents=" + cardUsedInCents +
               '}';
    }
}
