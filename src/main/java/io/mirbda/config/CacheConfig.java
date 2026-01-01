package io.mirbda.config;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.AsyncCache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String BLOCKCHAIN   = "blockchain";
    public static final String MEMPOOL      = "mempool";
    public static final String BLOCK        = "block";
    public static final String UTXO         = "utxo";
    public static final String TRANSACTION  = "transaction";
    public static final String NETWORK      = "network";
    public static final String MINING       = "mining";

    @Bean
    public CacheManager cacheManager() {
        final SimpleCacheManager manager = new SimpleCacheManager();

        manager.setCaches(
                List.of(
                        cache(BLOCKCHAIN,    120,  200),
                        cache(MEMPOOL,        30,   50),
                        cache(BLOCK,       86400, 1000),
                        cache(UTXO,          120,  300),
                        cache(TRANSACTION,  3600, 2000),
                        cache(NETWORK,        60,  200),
                        cache(MINING,        120,  200)
        ));

        return manager;
    }

    private static CaffeineCache cache(final String name, final long ttlSeconds, final long maxSize) {
        final AsyncCache<Object, Object> async = Caffeine.newBuilder()
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .buildAsync();

        return new CaffeineCache(name, async, true);
    }
}
