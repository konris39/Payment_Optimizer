package org.example.readersTests;

import org.example.models.PaymentMethod;
import org.example.readers.PaymentRead;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PaymentReadTest {

    @Test
    void read_valid_JsonFile_parsesMethods() throws IOException {
        String json = """
            [
              { "id": "PUNKTY", "discount": "15", "limit": "100,00" },
              { "id": "CARD1", "discount": "5", "limit": "200.50" }
            ]
            """;
        Path tmp = Files.createTempFile("payments", ".json");
        Files.writeString(tmp, json);

        List<PaymentMethod> methods = PaymentRead.read(tmp.toString());
        assertEquals(2, methods.size());

        PaymentMethod p = methods.stream()
                .filter(pm -> "PUNKTY".equals(pm.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(15, p.getDiscountPercentage());
        assertEquals(10000, p.getLimitInCents());
    }

    @Test
    void decrease_Limit_when_Over_Limit_throwsIllegalArgument() {
        PaymentMethod pm = new PaymentMethod("C1", "10", "5.00");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pm.decreaseLimit(600)
        );
        assertTrue(ex.getMessage().contains("No sufficient limit"));
    }

    @Test
    void read_samplePaymentMethods_parsesAllThreeCorrectly() throws IOException {
        String json = """
        [
          { "id": "PUNKTY", "discount": "15", "limit": "100.00" },
          { "id": "mZysk", "discount": "10", "limit": "180.00" },
          { "id": "BosBankrut", "discount": "5", "limit": "200.00" }
        ]
        """;
        Path tmp = Files.createTempFile("paymentmethods", ".json");
        Files.writeString(tmp, json);

        List<PaymentMethod> methods = PaymentRead.read(tmp.toString());
        assertEquals(3, methods.size());

        Map<String, PaymentMethod> map = methods.stream()
                .collect(Collectors.toMap(PaymentMethod::getId, pm -> pm));

        PaymentMethod p = map.get("PUNKTY");
        assertNotNull(p);
        assertEquals(15, p.getDiscountPercentage());
        assertEquals(10000, p.getLimitInCents());

        PaymentMethod mz = map.get("mZysk");
        assertNotNull(mz);
        assertEquals(10, mz.getDiscountPercentage());
        assertEquals(18000, mz.getLimitInCents());

        PaymentMethod bb = map.get("BosBankrut");
        assertNotNull(bb);
        assertEquals(5, bb.getDiscountPercentage());
        assertEquals(20000, bb.getLimitInCents());
    }

}
