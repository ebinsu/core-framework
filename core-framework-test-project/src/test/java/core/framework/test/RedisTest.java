package core.framework.test;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author ebin
 */
public class RedisTest {
    public static void main(String[] args) {
        RedisURI redisURI = RedisURI.Builder.redis("192.168.136.128", 30006).withDatabase(0).build();
        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        syncCommands.set("key", "Hello, Redis!");

        connection.close();
        redisClient.shutdown();
    }
}
