package com.bupt.sse.group7.covid19.utils;


import android.util.Log;

import com.bupt.sse.group7.covid19.interfaces.DAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DBConnector {
    private static String host = "http://47.103.5.100/";
    private static Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("http://47.103.5.100/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();
    public static DAO dao =retrofit.create(DAO.class);



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

    private static void executePost(String url, JsonObject content) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String response = "";
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("accept","application/json");
            Log.d("lyjDBArgs", content.toString());

            byte[] outBytes = content.toString().getBytes();

            conn.setRequestProperty("Content-Length", String.valueOf(outBytes.length));
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(outBytes);
            outputStream.flush();
            outputStream.close();

            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                response += lines;
            }
            reader.close();
            conn.disconnect();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonArray getStatistics() {
        return executeGET(host + "getStatistics.php");
    }
    public static JsonArray getTrackByIdAndDate(Map<String, String> args){
        String newURL = encapParamURL(host + "getTrackByIdAndDate.php", args);
        return executeGET(newURL);
    }
    public static JsonArray getTrackByDateAndTrack(Map<String, String> args) {
        String newURL = encapParamURL(host + "getTrackByDateAndDistrict.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getSuppliesById(Map<String, String> args) {
        String newURL = encapParamURL(host + "getSuppliesById.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getPatientAuthInfo(Map<String, String> args) {
        String newURL = encapParamURL(host + "getPatientAuthInfo.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getHospitalAuthInfo(Map<String, String> args) {
        String newURL = encapParamURL(host + "getHospitalAuthInfo.php", args);
        return executeGET(newURL);
    }

    public static JsonArray getTrackIds() {
        return executeGET(host + "getTrackIds.php");
    }

    private static String encapParamURL(String url, Map<String, String> args) {
        String newURL = url + "?";
        for (Map.Entry<String, String> entry : args.entrySet()) {
            newURL += entry.getKey() + "=" + entry.getValue() + "&";
        }
        return newURL.substring(0, newURL.length()-1);
    }

    public static void setUsername(JsonObject args) {
        executePost(host + "setPatientUsername.php", args);
    }

    public static JsonArray getAllTrackStarts() {
        return executeGET(host + "getAllTrackStarts.php");
    }

    public static void editHospitalById(JsonObject args) {
        executePost(host + "editHospitalById.php", args);
    }

    //----add----
    public static void editSuppliesById(JsonObject args) {
        executePost(host + "editSuppliesById.php", args);
    }

    public static void addPatientTrack(JsonObject args) {
        executePost(host + "addPatientTrack.php", args);
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

    public static JsonArray parseInfo(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        JsonObject outline = (JsonObject) JsonParser.parseString(sb.toString());
        JsonArray rows = (JsonArray) outline.get("rows");
        Log.d("lyjDBData", rows.toString());
        return rows;
    }

}