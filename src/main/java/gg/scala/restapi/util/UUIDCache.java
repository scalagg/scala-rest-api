package gg.scala.restapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class UUIDCache {

    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final LoadingCache<String, UUID> NAME_UUID_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public UUID load(String name) throws DataNotFoundException {
                    if (name.equalsIgnoreCase("CONSOLE"))
                        return new UUID(0, 0);

                    String response = getResponse("https://api.minetools.eu/uuid/" + name);
                    if (response == null)
                        throw new DataNotFoundException("Could not find UUID for " + name);

                    JsonObject parsed = JSON_PARSER.parse(response).getAsJsonObject();
                    if (!parsed.has("id") || parsed.get("id").isJsonNull())
                        throw new DataNotFoundException("Could not find UUID for " + name);

                    String uuid = parsed.get("id").getAsString().replace("-", "");

                    return UUID.fromString(insertHyphens(uuid));
                }
            });

    private static final LoadingCache<UUID, String> UUID_NAME_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public String load(UUID uuid) throws DataNotFoundException {
                    if (uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() == 0)
                        return "CONSOLE";

                    String response = getResponse("https://api.minetools.eu/uuid/" + uuid);
                    if (response == null)
                        throw new DataNotFoundException("Could not find name for " + uuid);

                    JsonObject parsed = JSON_PARSER.parse(response).getAsJsonObject();
                    if (!parsed.has("name") || parsed.get("name").isJsonNull())
                        throw new DataNotFoundException("Could not find name for " + uuid);

                    return parsed.get("name").getAsString();
                }
            });

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0" +
            "-9a-fA-F]{3}-[0-9a-fA-F]{12}");


    public static UUID getUuid(String name) {
        try {
            return NAME_UUID_CACHE.get(name);
        } catch (ExecutionException e) {
            if (!(e.getCause() instanceof DataNotFoundException))
                e.printStackTrace();
            return null;
        }
    }

    public static String getName(UUID uuid) {
        try {
            return UUID_NAME_CACHE.get(uuid);
        } catch (ExecutionException e) {
            if (!(e.getCause() instanceof DataNotFoundException))
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

