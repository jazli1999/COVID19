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
import java.util.Map;

public class DBConnector {
    private static String host = "http://47.103.5.100/";

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

    private static String encapParamURL(String url, Map<String, String> args) {
        String newURL = url + "?";
        for (Map.Entry<String, String> entry : args.entrySet()) {
            newURL += entry.getKey() + "=" + entry.getValue() + "&";
        }
        return newURL.substring(0, newURL.length()-1);
    }

    public static JsonArray getPatientTrackById(Map<String, String> args) {
        String newURL = encapParamURL(host + "getPatientTrackById.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getPatientById(Map<String, String> args) {
        String newURL = encapParamURL(host + "getPatientById.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getPStatusById(Map<String, String> args) {
        String newURL = encapParamURL(host + "getPStatusById.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getStatusNumberById(Map<String, String> args) {
        String newURL = encapParamURL(host + "getStatusNumberById.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getHospitalList() {
        return executeGET(host + "getHospitalList.php");
    }

    public static JsonArray getAllHospitals() {
        return executeGET(host + "getAllHospitals.php");
    }

    public static JsonArray getHospitalById(Map<String, String> args) {
        String newURL = encapParamURL(host + "getHospitalById.php", args);
        return executeGET(newURL);
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
        Log.d("DBData", rows.toString());
        return rows;
    }

}
