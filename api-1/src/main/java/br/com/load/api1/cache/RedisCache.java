package br.com.load.api1.cache;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Optional;

@ApplicationScoped
public class RedisCache {

    private final RedisClient redis;

    @Inject
    public RedisCache(RedisClient redis) {
        this.redis = redis;
    }

    public Optional<String> get(String key) {
        Response r = redis.get(key);
        if (r == null) return Optional.empty();
        return Optional.ofNullable(r.toString());
    }

    public void set(String key, String value, Duration ttl) {
        redis.setex(key, String.valueOf(ttl.getSeconds()), value);
    }

}
