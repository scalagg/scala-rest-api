package gg.scala.restapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class UUIDCache {

    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final LoadingCache<String, Optional<UUID>> NAME_UUID_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Optional<UUID> load(String name) {
                    if (name.equalsIgnoreCase("CONSOLE"))
                        return Optional.of(new UUID(0, 0));

                    // Try Mojang API first
                    Optional<UUID> mojangResult = getMojangUUID(name);
                    if (mojangResult.isPresent()) {
                        return mojangResult;
                    }

                    // Fallback to PlayerDB
                    Optional<UUID> playerDbResult = getPlayerDBUUID(name);
                    if (playerDbResult.isPresent()) {
                        return playerDbResult;
                    }

                    System.out.println("Could not find UUID for " + name + " using any available API");
                    return Optional.empty();
                }
            });

    private static final LoadingCache<UUID, Optional<String>> UUID_NAME_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Optional<String> load(UUID uuid) {
                    if (uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() == 0)
                        return Optional.of("CONSOLE");

                    // Try Mojang API first
                    Optional<String> mojangResult = getMojangName(uuid);
                    if (mojangResult.isPresent()) {
                        return mojangResult;
                    }

                    // Fallback to PlayerDB
                    Optional<String> playerDbResult = getPlayerDBName(uuid);
                    if (playerDbResult.isPresent()) {
                        return playerDbResult;
                    }

                    System.out.println("Could not find name for " + uuid + " using any available API");
                    return Optional.empty();
                }
            });

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

    private static Optional<UUID> getMojangUUID(String name) {
        try {
            String response = getResponse("https://api.mojang.com/users/profiles/minecraft/" + name);
            if (response == null) return Optional.empty();

            JsonObject parsed = JSON_PARSER.parse(response).getAsJsonObject();
            if (!parsed.has("id") || parsed.get("id").isJsonNull()) {
                return Optional.empty();
            }

            String uuid = parsed.get("id").getAsString();
            return Optional.of(UUID.fromString(insertHyphens(uuid)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<String> getMojangName(UUID uuid) {
        try {
            String response = getResponse("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
            if (response == null) return Optional.empty();

            JsonObject parsed = JSON_PARSER.parse(response).getAsJsonObject();
            if (!parsed.has("name") || parsed.get("name").isJsonNull()) {
                return Optional.empty();
            }

            return Optional.of(parsed.get("name").getAsString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<UUID> getPlayerDBUUID(String name) {
        try {
            String response = getResponse("https://playerdb.co/api/player/minecraft/" + name);
            if (response == null) return Optional.empty();

            JsonObject parsed = JSON_PARSER.parse(response).getAsJsonObject();
            if (!parsed.has("data") || !parsed.getAsJsonObject("data").has("player")) {
                return Optional.empty();
            }

            JsonObject player = parsed.getAsJsonObject("data").getAsJsonObject("player");
            if (!player.has("id") || player.get("id").isJsonNull()) {
                return Optional.empty();
            }

            String uuid = player.get("id").getAsString().replace("-", "");
            return Optional.of(UUID.fromString(insertHyphens(uuid)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<String> getPlayerDBName(UUID uuid) {
        try {
            String response = getResponse("https://playerdb.co/api/player/minecraft/" + uuid.toString());
            if (response == null) return Optional.empty();

            JsonObject parsed = JSON_PARSER.parse(response).getAsJsonObject();
            if (!parsed.has("data") || !parsed.getAsJsonObject("data").has("player")) {
                return Optional.empty();
            }

            JsonObject player = parsed.getAsJsonObject("data").getAsJsonObject("player");
            if (!player.has("username") || player.get("username").isJsonNull()) {
                return Optional.empty();
            }

            return Optional.of(player.get("username").getAsString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static UUID getUuid(String name) {
        try {
            return NAME_UUID_CACHE.get(name).orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getName(UUID uuid) {
        try {
            return UUID_NAME_CACHE.get(uuid).orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* TODO: whne I do redis
    public static String getName(UUID uuid) {
        return InvictusRestAPI.getInstance().getRedisService().executeCommand(redis -> {
            if (!redis.hexists("UUIDCache", uuid.toString()))
                return null;

            return redis.hget("UUIDCache", uuid.toString());
        });
    }
     */

    public static boolean isUuid(String input) {
        return UUID_PATTERN.matcher(input).matches();
    }

    private static String getResponse(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().forEach(response::append);
                return response.toString();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static String insertHyphens(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }
}