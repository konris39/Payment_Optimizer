package org.example.serviceTests;

import org.example.models.AllocationType;
import org.example.models.Order;
import org.example.models.PaymentMethod;
import org.example.services.AllocService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AllocServiceTest {

    private AllocService service;

    @BeforeEach
    void setUp() {
        service = new AllocService();
    }

    @Test
    void fullCardPromotion_appliesBestCard() {
        Order o = new Order("O", "100.00", Set.of("CARD10"));
        PaymentMethod card = new PaymentMethod("CARD10", "10",  "1000.00");
        PaymentMethod points = new PaymentMethod("PUNKTY", "15", "0.00");

        List<AllocationType> allocs = service.allocate(List.of(o), List.of(card, points));

        assertEquals(1, allocs.size());
        AllocationType a = allocs.get(0);
        assertEquals("O", a.getOrderId());
        assertEquals("CARD10", a.getPaymentMethodId());
        assertEquals(9000, a.getCardUsedInCents());
        assertEquals(0, a.getPointsUsedInCents());
    }

    @Test
    void fullPointsPromotion_usesPUNKTYDiscount() {
        Order o = new Order("O", "50.00", Set.of());
        PaymentMethod points = new PaymentMethod("PUNKTY", "15", "100.00");
        PaymentMethod card = new PaymentMethod("C", "10", "100.00");

        List<AllocationType> allocs = service.allocate(List.of(o), List.of(points, card));

        AllocationType a = allocs.stream()
                .filter(x -> "PUNKTY".equals(x.getPaymentMethodId()))
                .findFirst()
                .orElseThrow();
        assertEquals(4250, a.getPointsUsedInCents());
    }

    @Test
    void partialPoints_thenCard_with10PercentExtra() {
        Order o = new Order("O", "100.00", null);
        PaymentMethod points = new PaymentMethod("PUNKTY", "15", "20.00");
        PaymentMethod card = new PaymentMethod("C1", "0", "100.00");

        List<AllocationType> allocs = service.allocate(List.of(o), List.of(points, card));

        long totalPts = allocs.stream()
                .filter(x -> "PUNKTY".equals(x.getPaymentMethodId()))
                .mapToLong(AllocationType::getPointsUsedInCents)
                .sum();
        long totalCard = allocs.stream()
                .filter(x -> !"PUNKTY".equals(x.getPaymentMethodId()))
                .mapToLong(AllocationType::getCardUsedInCents)
                .sum();
        assertEquals(2000, totalPts);
        assertEquals(7000, totalCard);
    }

    @Test
    void insufficientFunds_throwsIllegalState() {
        Order o = new Order("O", "100.00", Set.of());
        PaymentMethod points = new PaymentMethod("PUNKTY", "10", "5.00");
        PaymentMethod card = new PaymentMethod("C", "0", "0.00");

        assertThrows(
                IllegalStateException.class,
                () -> service.allocate(List.of(o), List.of(points, card))
        );
    }

    @Test
    void sampleScenario_returnsExpectedTotals() {
        List<Order> orders = List.of(
                new Order("ORDER1", "100.00", Set.of("mZysk")),
                new Order("ORDER2", "200.00", Set.of("BosBankrut")),
                new Order("ORDER3", "150.00", Set.of("mZysk", "BosBankrut")),
                new Order("ORDER4", "50.00", null)
        );
        List<PaymentMethod> methods = List.of(
                new PaymentMethod("PUNKTY", "15", "100.00"),
                new PaymentMethod("mZysk", "10", "180.00"),
                new PaymentMethod("BosBankrut", "5", "200.00")
        );

        List<AllocationType> allocs = service.allocate(orders, methods);

        Map<String, Long> summary = allocs.stream()
                .collect(Collectors.groupingBy(
                        AllocationType::getPaymentMethodId,
                        Collectors.summingLong(a ->
                                "PUNKTY".equals(a.getPaymentMethodId())
                                        ? a.getPointsUsedInCents() : a.getCardUsedInCents()
                        )
                ));

        assertEquals(10000L, summary.get("PUNKTY"));
        assertEquals(19000L, summary.get("BosBankrut"));
        assertEquals(16500L, summary.get("mZysk"));
    }
}
