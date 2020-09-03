package com.bupt.sse.group7.covid19.presenter;

import com.bupt.sse.group7.covid19.interfaces.IPatientViewCallBack;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.model.Status;
import com.bupt.sse.group7.covid19.model.TrackPoint;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientPresenter {
    // TODO 添加数据获取失败时的处理

    private static PatientPresenter instance = new PatientPresenter();
    private List<IPatientViewCallBack> callBacks = new ArrayList<>();

    private Patient patient;
    private JsonObject patientResult; //pre
    private JsonArray pStatusResult; //pre
    private JsonArray tracksResult; //pre


    PatientPresenter() {
        patient = new Patient();
    }

    public void getPatientInfo() {
        Thread thread = getPatientInfo(this.patient.getId());
        try {
            thread.join();
            processResults();
            handlePatientInfoResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handlePatientInfoResult() {
        for (IPatientViewCallBack callBack : callBacks) {
            callBack.onPatientInfoReturned(this.patient);
        }
    }

    public void registerCallBack(IPatientViewCallBack callBack) {
        if (callBacks != null && !callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    public void unregisterCallBack(IPatientViewCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    private Thread getPatientInfo(int p_id) {
        Map<String, String> args = new HashMap<>();
        args.put("p_id", String.valueOf(p_id));
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        patientResult = DBConnector.getPatientById(args).get(0).getAsJsonObject();
                        pStatusResult = DBConnector.getPStatusById(args);
                        tracksResult = DBConnector.getPatientTrackById(args);
                    }
                });
        thread.start();
        return thread;
    }

    private void processResults() {
        // parse data and assign
        this.patient.setH_name(patientResult.get("h_name").getAsString());
        this.patient.setUsername(patientResult.get("username").getAsString());
        this.patient.setStatus(patientResult.get("status").getAsString());
        for(JsonElement je: tracksResult) {
            this.patient.getTrackPoints().add(
                    new TrackPoint(je.getAsJsonObject().get("date_time").getAsString(),
                            je.getAsJsonObject().get("location").getAsString(),
                            je.getAsJsonObject().get("description").getAsString()));
        }
        for (JsonElement je: pStatusResult) {
            this.patient.getStatuses().add(
                    new Status(je.getAsJsonObject().get("day").getAsString(),
                            je.getAsJsonObject().get("status").getAsInt())
            );
        }
    }

    public static PatientPresenter getInstance() {
        return instance;
    }

    public void setPatientId(int id) {
        this.patient.setId(id);
    }
}

