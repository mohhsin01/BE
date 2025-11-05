package com.ltv.saas.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        // Caffeine cache with 30-second TTL (Time To Live)
        // Cache entries expire after 30 seconds and are automatically removed
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("sellerSummary");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)  // Cache expires 30 seconds after write
            .maximumSize(1000));  // Maximum 1000 cached entries
        return cacheManager;
    }
}

