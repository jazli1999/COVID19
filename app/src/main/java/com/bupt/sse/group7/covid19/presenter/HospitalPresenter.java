package com.bupt.sse.group7.covid19.presenter;

import android.util.Log;

import com.bupt.sse.group7.covid19.interfaces.IDataBackCallBack;
import com.bupt.sse.group7.covid19.interfaces.IHospitalViewCallBack;
import com.bupt.sse.group7.covid19.model.Hospital;
import com.bupt.sse.group7.covid19.model.Statistics;
import com.bupt.sse.group7.covid19.model.Supplies;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bupt.sse.group7.covid19.utils.JsonUtils.safeGet;

public class HospitalPresenter implements IDataBackCallBack {

    private final String TAG="HospitalPresenter";
    private Hospital hospital;

    private JsonObject hospitalResult;
    private JsonObject statisticsResult;
    private JsonObject suppliesResult;
    private final int dataCount=3;
    private int dataSize=0;

    private List<IHospitalViewCallBack> callBacks = new ArrayList<>();

    private static HospitalPresenter instance = new HospitalPresenter();

    HospitalPresenter() {
        hospital = new Hospital();
    }

    public static HospitalPresenter getInstance() {
        return instance;
    }


    private void getHospitalResult(){
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(hospital.getId()));
        Call<ResponseBody> data = DBConnector.dao.getData("getHospitalById.php",args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    hospitalResult= JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getHospitalResultOnFailure");

            }
        });

    }
    private void getStatisticsResult(){
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(hospital.getId()));
        Call<ResponseBody> data = DBConnector.dao.getData("getStatusNumberById.php",args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    statisticsResult=JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getStatisticsResultOnFailure");

            }
        });
    }
    private void getSuppliesResult(){
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(hospital.getId()));
        Call<ResponseBody> data = DBConnector.dao.getData("getSuppliesById.php",args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JsonArray temp=JsonUtils.parseInfo(response.body().byteStream());
                    if (temp != null && temp.size() > 0) {
                        //如果该医院有物资信息，如果没有的话supplies始终为空
                        suppliesResult = temp.get(0).getAsJsonObject();
                        //返回supplies的json对象
                    } else {
                        suppliesResult = new JsonObject();
                    }
                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getSuppliesResultOnFailure");

            }
        });

    }
    public void getHospitalDetails() {
        dataSize=0;
        getHospitalResult();
        getStatisticsResult();
        getSuppliesResult();
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


    //第二个参数中包含supplies的所有信息
    public void updateData(JsonObject args,JsonObject args2) {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        DBConnector.editHospitalById(args);
                        DBConnector.editSuppliesById(args2);
                    }
                }
        );
        try {
            thread.start();
            thread.join();
            getHospitalDetails();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAllDataBack() {
        dataSize++;
        if(dataSize==dataCount){
            processResults();
            handlePatientInfoResults();

        }
    }
}
