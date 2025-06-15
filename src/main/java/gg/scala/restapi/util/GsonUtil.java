package gg.scala.restapi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonUtil {
    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();

    public static final Gson COMPACT_GSON = new GsonBuilder()
        .serializeNulls()
        .create();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static String toCompactJson(Object object) {
        return COMPACT_GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static JsonElement parseJson(String json) {
        return JsonParser.parseString(json);
    }
} 