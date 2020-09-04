package com.bupt.sse.group7.covid19.presenter;

import com.bupt.sse.group7.covid19.interfaces.IHospitalViewCallBack;
import com.bupt.sse.group7.covid19.model.Hospital;
import com.bupt.sse.group7.covid19.model.Statistics;
import com.bupt.sse.group7.covid19.model.Supplies;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bupt.sse.group7.covid19.utils.JsonUtils.safeGet;

public class HospitalPresenter {

    private Hospital hospital;

    private JsonObject hospitalResult;
    private JsonObject statisticsResult;
    private JsonObject suppliesResult;

    private List<IHospitalViewCallBack> callBacks = new ArrayList<>();

    private static HospitalPresenter instance = new HospitalPresenter();

    HospitalPresenter() {
        hospital = new Hospital();
    }

    public static HospitalPresenter getInstance() {
        return instance;
    }

    public void getHospitalDetails() {
        Thread thread = getHospitalInfo(this.hospital.getId());
        try {
            thread.join();
            processResults();
            handlePatientInfoResults();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handlePatientInfoResults() {
        for (IHospitalViewCallBack callBack: callBacks) {
            callBack.onHospitalInfoReturned(hospital);
        }
    }

    public void registerCallBack(IHospitalViewCallBack callBack) {
        callBacks.add(callBack);
    }

    public void unregisterCallBack(IHospitalViewCallBack callBack) {
        callBacks.remove(callBack);
    }

    private void processResults() {
        hospital.setAddress(safeGet(hospitalResult, "address"));
        hospital.setTel(safeGet(hospitalResult, "tel"));
        hospital.setMildLeft(safeGet(hospitalResult, "mild_left"));
        hospital.setSevereLeft(safeGet(hospitalResult, "severe_left"));
        hospital.setName(safeGet(hospitalResult, "name"));
        hospital.setPeople(safeGet(hospitalResult, "people"));

        hospital.setStatistics(new Statistics(safeGet(statisticsResult, "mild"),
                safeGet(statisticsResult, "severe"),
                safeGet(statisticsResult, "cured"),
                safeGet(statisticsResult, "dead")));
        hospital.setSupplies(new Supplies(safeGet(suppliesResult, "n95"),
                safeGet(suppliesResult, "surgeon"),
                safeGet(suppliesResult, "ventilator"),
                safeGet(suppliesResult, "clothe"),
                safeGet(suppliesResult, "glasses"),
                safeGet(suppliesResult, "alcohol"),
                safeGet(suppliesResult, "pants")));
    }

    public void setID(int id) {
        hospital.setId(id);
    }

    private Thread getHospitalInfo(int h_id) {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(h_id));
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        hospitalResult = DBConnector.getHospitalById(args).get(0).getAsJsonObject();
                        statisticsResult = DBConnector.getStatusNumberById(args).get(0).getAsJsonObject();
                        JsonArray temp = DBConnector.getSuppliesById(args);
                        if (temp != null && temp.size() > 0) {
                            //如果该医院有物资信息，如果没有的话supplies始终为空
                            suppliesResult = temp.get(0).getAsJsonObject();
                            //返回supplies的json对象
                        } else {
                            suppliesResult = new JsonObject();
                        }
                    }
                }
        );
        thread.start();
        return thread;
    }
}
