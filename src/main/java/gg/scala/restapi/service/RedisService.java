package gg.scala.restapi.service;

import redis.clients.jedis.Jedis;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.function.Function;

@Service
public class RedisService {
    private final JedisPool jedisPool;

    public RedisService(
        @Value("${redis.host:localhost}") String host,
        @Value("${redis.port:6379}") int port,
        @Value("${redis.password:}") String password
    ) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        if (password != null && !password.isEmpty()) {
            this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            this.jedisPool = new JedisPool(poolConfig, host, port, 2000);
        }
    }

    public <T> T executeCommand(Function<Jedis, T> command) {
        try (Jedis jedis = jedisPool.getResource()) {
            return command.apply(jedis);
        }
    }

    public void executeCommandVoid(Function<Jedis, Void> command) {
        try (Jedis jedis = jedisPool.getResource()) {
            command.apply(jedis);
        }
    }
} 