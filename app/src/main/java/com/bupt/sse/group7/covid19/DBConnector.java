package com.bupt.sse.group7.covid19;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBConnector {
    private static String getAllHospitalsURL = "http://47.103.5.100/getAllHospitals.php";

    private static JsonArray executeGET(String url) {
        HttpURLConnection conn = null;
        InputStream info = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200) {
                info = conn.getInputStream();
                return parseInfo(info);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonArray getAllHospitals() {
        return executeGET(getAllHospitalsURL);
    }

    private static JsonArray parseInfo(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        JsonObject outline = (JsonObject) JsonParser.parseString(sb.toString());
        JsonArray rows = (JsonArray) outline.get("rows");
        Log.d("Info", rows.toString());
        return rows;
    }
}
