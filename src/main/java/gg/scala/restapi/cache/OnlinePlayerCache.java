package gg.scala.restapi.cache;

import com.google.gson.JsonObject;
import gg.scala.restapi.util.GsonUtil;
import gg.scala.restapi.service.RedisService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class OnlinePlayerCache {
    private final RedisService redisService;
    private Map<String, String> onlinePlayers = new HashMap<>();
    private long lastUpdate = 0;
    private static final long CACHE_DURATION = TimeUnit.SECONDS.toMillis(10);

    public OnlinePlayerCache(RedisService redisService) {
        this.redisService = redisService;
    }

    public Map<String, String> getOnlinePlayers() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate > CACHE_DURATION) {
            updateOnlinePlayers();
            lastUpdate = now;
        }
        return onlinePlayers;
    }

    private void updateOnlinePlayers() {
        Map<String, String> newOnlinePlayers = new HashMap<>();
        redisService.executeCommand(jedis -> {
            Map<String, String> heartbeats = jedis.hgetAll("serversync:heartbeats");
            Map<String, String> servers = jedis.hgetAll("serversync:serversv2");

            servers.forEach((server, data) -> {
                JsonObject object = GsonUtil.GSON.fromJson(data, JsonObject.class);
                
                // Skip if server heartbeat is expired
                if (heartbeats.containsKey(server) && 
                    System.currentTimeMillis() > Long.parseLong(heartbeats.get(server))) {
                    return;
                }

                // Get online players from server metadata
                object.getAsJsonObject("properties")
                      .getAsJsonArray("metadata")
                      .forEach(element -> {
                          JsonObject metadata = element.getAsJsonObject();
                          if (metadata.get("first").getAsString().equals("server:online-list")) {
                              metadata.getAsJsonObject("second")
                                     .getAsJsonArray("value")
                                     .forEach(player -> {
                                         newOnlinePlayers.put(player.getAsString(), server);
                                     });
                          }
                      });
            });
            return null;
        });

        onlinePlayers = newOnlinePlayers;
    }
} 