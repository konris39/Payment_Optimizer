package org.example;

import org.example.models.AllocationType;
import org.example.models.Order;
import org.example.models.PaymentMethod;
import org.example.readers.OrderRead;
import org.example.readers.PaymentRead;
import org.example.services.AllocService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("To run app please make sure to type: java -jar app.jar Path/for/orders.json Path/for/paymentmethods.json");
            System.exit(1);
        }
        String ordersPath = args[0];
        String paymentsPath = args[1];

        try {
            List<Order> orders = OrderRead.read(ordersPath);
            List<PaymentMethod> methods = PaymentRead.read(paymentsPath);
            AllocService allocService = new AllocService();
            List<AllocationType> allocations = allocService.allocate(orders, methods);

            Map<String, Long> paymentsSummary = allocations.stream()
                .collect(Collectors.groupingBy(
                    AllocationType::getPaymentMethodId,
                    Collectors.summingLong(a ->
                        "PUNKTY".equals(a.getPaymentMethodId())
                            ? a.getPointsUsedInCents() : a.getCardUsedInCents()
                    )
                ));

            paymentsSummary.forEach((method, amountInCents) ->
                System.out.printf("%s %.2f%n", method, amountInCents / 100.0)
            );
        } catch (Exception e) {
            System.err.println("Error during processing:");
            e.printStackTrace();
            System.exit(2);
        }
    }
}
