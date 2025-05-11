package org.example.readers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.PaymentMethod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PaymentRead {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<PaymentMethod> read(String filePath) throws IOException {
        try (InputStream in = Files.newInputStream(Path.of(filePath))) {
            return mapper.readValue(in, new TypeReference<List<PaymentMethod>>() {});
        }
    }
}
