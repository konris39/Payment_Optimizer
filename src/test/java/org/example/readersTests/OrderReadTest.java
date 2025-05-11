package org.example.readersTests;

import org.example.models.Order;
import org.example.readers.OrderRead;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OrderReadTest {

    @Test
    void read_valid_JsonFile_parsesOrders() throws IOException {
        String json = """
            [
              { "id": "Order1", "value": "221.45", "promotions": ["prom1","niceProm"] },
              { "id": "Order2", "value": "23.00" }
            ]
            """;
        Path tmp = Files.createTempFile("orders", ".json");
        Files.writeString(tmp, json);

        List<Order> orders = OrderRead.read(tmp.toString());
        assertEquals(2, orders.size());

        Order order1 = orders.stream().filter(o -> "Order1".equals(o.getId())).findFirst().orElseThrow();
        assertEquals(22145, order1.getValueInCents());
        assertEquals(Set.of("prom1", "niceProm"), order1.getPromotions());

        Order order2 = orders.stream().filter(o -> "Order2".equals(o.getId())).findFirst().orElseThrow();
        assertEquals(2300, order2.getValueInCents());
        assertTrue(order2.getPromotions().isEmpty());
    }

     @Test
    void read_sampleOrdersJson_parsesAllOrders() throws IOException {
        String json = """
            [
              { "id": "ORDER1", "value": "100.00", "promotions": ["mZysk"] },
              { "id": "ORDER2", "value": "200.00", "promotions": ["BosBankrut"] },
              { "id": "ORDER3", "value": "150.00", "promotions": ["mZysk", "BosBankrut"] },
              { "id": "ORDER4", "value": "50.00" }
            ]
            """;
        Path tmp = Files.createTempFile("orders", ".json");
        Files.writeString(tmp, json);

        List<Order> orders = OrderRead.read(tmp.toString());

        assertEquals(4, orders.size());

        Map<String, Order> map = orders.stream()
            .collect(Collectors.toMap(Order::getId, o -> o));

        Order o1 = map.get("ORDER1");
        assertNotNull(o1);
        assertEquals(10000, o1.getValueInCents());
        assertEquals(Set.of("mZysk"), o1.getPromotions());

        Order o2 = map.get("ORDER2");
        assertNotNull(o2);
        assertEquals(20000, o2.getValueInCents());
        assertEquals(Set.of("BosBankrut"), o2.getPromotions());

        Order o3 = map.get("ORDER3");
        assertNotNull(o3);
        assertEquals(15000, o3.getValueInCents());
        assertEquals(Set.of("mZysk", "BosBankrut"), o3.getPromotions());

        Order o4 = map.get("ORDER4");
        assertNotNull(o4);
        assertEquals(5000, o4.getValueInCents());
        assertTrue(o4.getPromotions().isEmpty());
    }
}
