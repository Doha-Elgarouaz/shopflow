package com.shopflow.product.config;

import com.shopflow.product.entity.Product;
import com.shopflow.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner initData(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) {
                log.info("Database already seeded, skipping initialization.");
                return;
            }

            log.info("Seeding product catalog...");

            List<Product> products = List.of(
                Product.builder()
                    .name("MacBook Pro 16\" M3 Pro")
                    .description("Apple MacBook Pro 16-inch with M3 Pro chip, 18GB RAM, 512GB SSD. Perfect for developers and creatives.")
                    .sku("APPLE-MBP-M3-16")
                    .price(new BigDecimal("2999.99"))
                    .stockQuantity(15)
                    .category("Electronics")
                    .imageUrl("https://store.storeimages.cdn-apple.com/macbook-pro-m3.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Sony WH-1000XM5 Headphones")
                    .description("Industry-leading noise canceling headphones with 30-hour battery life and crystal clear hands-free calling.")
                    .sku("SONY-WH1000XM5-BK")
                    .price(new BigDecimal("349.99"))
                    .stockQuantity(42)
                    .category("Electronics")
                    .imageUrl("https://www.sony.com/wh1000xm5.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Samsung 4K OLED Monitor 27\"")
                    .description("27-inch 4K OLED display, 144Hz refresh rate, 0.1ms response time. Ideal for gaming and professional work.")
                    .sku("SAMSUNG-OLED-27-4K")
                    .price(new BigDecimal("799.99"))
                    .stockQuantity(8)
                    .category("Electronics")
                    .imageUrl("https://samsung.com/oled-monitor.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Nike Air Max 270 React")
                    .description("Lightweight running shoe with Air Max 270 cushioning and React foam midsole. Available in multiple colors.")
                    .sku("NIKE-AM270-REACT-42")
                    .price(new BigDecimal("149.99"))
                    .stockQuantity(65)
                    .category("Sports")
                    .imageUrl("https://nike.com/air-max-270.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Clean Code — Robert C. Martin")
                    .description("A handbook of agile software craftsmanship. Learn to write clean, maintainable code with proven principles.")
                    .sku("BOOK-CLEANCODE-MARTIN")
                    .price(new BigDecimal("34.99"))
                    .stockQuantity(100)
                    .category("Books")
                    .imageUrl("https://images.amazon.com/clean-code.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Designing Data-Intensive Applications")
                    .description("The big ideas behind reliable, scalable, and maintainable systems. Essential reading for backend engineers.")
                    .sku("BOOK-DDIA-KLEPPMANN")
                    .price(new BigDecimal("54.99"))
                    .stockQuantity(75)
                    .category("Books")
                    .imageUrl("https://images.amazon.com/ddia.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Ergonomic Office Chair Pro")
                    .description("Lumbar support, adjustable armrests, breathable mesh back. Work comfortably for hours.")
                    .sku("CHAIR-ERGO-PRO-BK")
                    .price(new BigDecimal("449.99"))
                    .stockQuantity(20)
                    .category("Home")
                    .imageUrl("https://example.com/ergo-chair.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Keychron K2 Mechanical Keyboard")
                    .description("75% compact wireless mechanical keyboard. Compatible with Mac and Windows. Gateron Brown switches.")
                    .sku("KEYCHRON-K2-BROWN")
                    .price(new BigDecimal("89.99"))
                    .stockQuantity(33)
                    .category("Electronics")
                    .imageUrl("https://keychron.com/k2.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Uniqlo Merino Wool Crew Neck")
                    .description("Premium merino wool crew neck sweater. Soft, warm, and machine washable. Available in 12 colors.")
                    .sku("UNIQLO-MERINO-CREW-M")
                    .price(new BigDecimal("59.99"))
                    .stockQuantity(3)
                    .category("Clothing")
                    .imageUrl("https://uniqlo.com/merino.jpg")
                    .active(true)
                    .build(),
                Product.builder()
                    .name("Yoga Mat Premium 6mm")
                    .description("Non-slip natural rubber yoga mat, 6mm thickness, eco-friendly. Includes carrying strap.")
                    .sku("YOGA-MAT-6MM-PURPLE")
                    .price(new BigDecimal("49.99"))
                    .stockQuantity(55)
                    .category("Sports")
                    .imageUrl("https://example.com/yoga-mat.jpg")
                    .active(true)
                    .build()
            );

            List<Product> saved = productRepository.saveAll(products);
            log.info("✅ Seeded {} products successfully.", saved.size());
        };
    }
}
