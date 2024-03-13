package com.example.EventGenerator.Service;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
public class DataGeneratorService {

    public final Faker faker = new Faker();


    public String generateProductViewedEvent() {
        String userId = String.valueOf(faker.number().numberBetween(1, 10));
        String productId = String.valueOf(faker.number().numberBetween(1, 100));
        String timestamp = Instant.now().toString();
        return String.format("{\"eventType\":\"ProductViewed\",\"userId\":\"%s\",\"productId\":\"%s\",\"timestamp\":\"%s\"}", userId, productId, timestamp);
    }

    public String generateProductAddedToCartEvent() {
        String userId = String.valueOf(faker.number().numberBetween(1, 10));
        String productId = String.valueOf(faker.number().numberBetween(1, 100));
        int quantity = faker.number().numberBetween(1, 5);
        String timestamp = Instant.now().toString();
        return String.format("{\"eventType\":\"ProductAddedToCart\",\"userId\":\"%s\",\"productId\":\"%s\",\"quantity\":%d,\"timestamp\":\"%s\"}", userId, productId, quantity, timestamp);
    }

    public String generateProductPurchasedEvent() {
        String userId = String.valueOf(faker.number().numberBetween(1, 10));
        String productId = String.valueOf(faker.number().numberBetween(1, 100));
        double price = faker.number().randomDouble(2, 10, 500);
        String formattedPrice = String.format(Locale.US, "%.2f", price); // Ensure dot as decimal separator
        String timestamp = Instant.now().toString();
        return String.format("{\"eventType\":\"ProductPurchased\",\"userId\":\"%s\",\"productId\":\"%s\",\"price\":%s,\"timestamp\":\"%s\"}", userId, productId, formattedPrice, timestamp);
    }

    public String generateProductFavoritedEvent() {
        String userId = String.valueOf(faker.number().numberBetween(1, 10));
        String productId = String.valueOf(faker.number().numberBetween(1, 100));
        String timestamp = Instant.now().toString();
        return String.format("{\"eventType\":\"ProductFavorited\",\"userId\":\"%s\",\"productId\":\"%s\",\"timestamp\":\"%s\"}", userId, productId, timestamp);
    }
}
