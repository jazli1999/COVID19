package com.bupt.sse.group7.covid19.utils;

import com.google.gson.JsonObject;

public class JsonUtils {
    public static String safeGet(JsonObject obj, String arg) {
        if (obj.get(arg) == null || obj.get(arg).isJsonNull()) {
            return "-";
        }
        else {
            return obj.get(arg).getAsString();
        }
    }
}
