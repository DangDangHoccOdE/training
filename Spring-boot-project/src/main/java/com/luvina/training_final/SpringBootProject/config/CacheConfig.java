package com.luvina.training_final.SpringBootProject.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Tạo cấu hình cache
        var ehCacheConfig = Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Object.class, Object.class,
                                ResourcePoolsBuilder.heap(100)
                        )
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(3600)))
                        .build()
        );

        // Lấy CacheManager JSR-107
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

        // Tạo cache và bật thống kê
        javax.cache.Cache<Object, Object> cache = cacheManager.createCache("myCache", ehCacheConfig);
        org.ehcache.core.Ehcache ehcache = cache.unwrap(org.ehcache.core.Ehcache.class);
        ehcache.getRuntimeConfiguration().registerCacheEventListener(
                event -> System.out.println("Cache Event: " + event.getType()),
                EventOrdering.ORDERED,
                EventFiring.ASYNCHRONOUS,
                EventType.CREATED
        );

        // Kích hoạt thống kê
        cacheManager.enableStatistics("myCache", true);

        return cacheManager;
    }


    @Bean
    public org.springframework.cache.CacheManager springCacheManager(CacheManager cacheManager) {
        return new JCacheCacheManager(cacheManager);
    }
}





