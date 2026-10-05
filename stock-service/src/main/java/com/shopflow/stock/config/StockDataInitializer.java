package com.shopflow.stock.config;

import com.shopflow.stock.model.StockItem;
import com.shopflow.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StockDataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner initStockData(StockRepository stockRepository) {
        return args -> {
            if (stockRepository.count() > 0) {
                log.info("Stock already initialized, skipping.");
                return;
            }

            log.info("Initializing stock levels...");

            // productId matches the auto-generated IDs from product-service seeder (1-10)
            List<StockItem> stockItems = List.of(
                buildStock(1L, "MacBook Pro 16\" M3 Pro",       "APPLE-MBP-M3-16",      15),
                buildStock(2L, "Sony WH-1000XM5 Headphones",    "SONY-WH1000XM5-BK",    42),
                buildStock(3L, "Samsung 4K OLED Monitor 27\"",  "SAMSUNG-OLED-27-4K",    8),
                buildStock(4L, "Nike Air Max 270 React",         "NIKE-AM270-REACT-42",  65),
                buildStock(5L, "Clean Code",                     "BOOK-CLEANCODE-MARTIN",100),
                buildStock(6L, "Designing Data-Intensive Apps",  "BOOK-DDIA-KLEPPMANN",  75),
                buildStock(7L, "Ergonomic Office Chair Pro",     "CHAIR-ERGO-PRO-BK",    20),
                buildStock(8L, "Keychron K2 Mechanical Keyboard","KEYCHRON-K2-BROWN",    33),
                buildStock(9L, "Uniqlo Merino Wool Crew Neck",   "UNIQLO-MERINO-CREW-M",  3),
                buildStock(10L,"Yoga Mat Premium 6mm",           "YOGA-MAT-6MM-PURPLE",  55)
            );

            stockRepository.saveAll(stockItems);
            log.info("✅ Stock initialized for {} products.", stockItems.size());
        };
    }

    private StockItem buildStock(Long productId, String name, String sku, int qty) {
        StockItem item = new StockItem();
        item.setProductId(productId);
        item.setProductName(name);
        item.setSku(sku);
        item.setAvailableQuantity(qty);
        item.setReservedQuantity(0);
        item.setLastUpdated(LocalDateTime.now());
        return item;
    }
}
