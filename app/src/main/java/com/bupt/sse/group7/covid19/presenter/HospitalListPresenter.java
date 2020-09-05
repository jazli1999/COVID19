package com.bupt.sse.group7.covid19.presenter;

import android.content.res.XmlResourceParser;
import android.util.Log;

import com.bupt.sse.group7.covid19.interfaces.IHospitalListCallBack;
import com.bupt.sse.group7.covid19.model.City;
import com.bupt.sse.group7.covid19.model.District;

import com.bupt.sse.group7.covid19.model.HospitalBrief;
import com.bupt.sse.group7.covid19.model.Province;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

public class HospitalListPresenter {
    private static final String TAG = "HospitalListPresenter";
    private List<Province> pList = null;
    private String areaId;
    private static HospitalListPresenter instance = new HospitalListPresenter();
    private List<IHospitalListCallBack> callBacks = new ArrayList<>();

    public static HospitalListPresenter getInstance() {
        return instance;
    }

    public void registerCallBack(IHospitalListCallBack callBack) {
        if (callBacks != null && !callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    public void unregisterCallBack(IHospitalListCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    public void updateData(List<HospitalBrief> hospitals) {
        for(IHospitalListCallBack callBack: callBacks) {
            callBack.onHospitalListChanged(hospitals);
        }
    }

    public void setAreaId(String id) {
        this.areaId = id;
    }

    public void initData() {
        Call<ResponseBody> data;
        Map<String, String> args = new HashMap<>();
        if (areaId.length() == 4) {
            args.put("city", areaId);
            Log.d(TAG, "initData: " + areaId);
            data = DBConnector.dao.getData("getHospitalListByCity.php", args);
        } else {
            args.put("district", areaId);
            Log.d(TAG, "initData: " + areaId);
            data = DBConnector.dao.getData("getHospitalListByDistrict.php", args);
        }
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    List<HospitalBrief> hospitals = processResult(JsonUtils.parseInfo(response.body().byteStream()));
                    updateData(hospitals);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "HospitalListActivityOnFailure");
            }
        });
    }

    private List<HospitalBrief> processResult(JsonArray hospJson) {
        List<HospitalBrief> hospitals = new ArrayList<>();
        for (JsonElement hospital: hospJson) {
            JsonObject hospitalJson = hospital.getAsJsonObject();
            HospitalBrief hospitalBrief = new HospitalBrief(hospitalJson.get("h_id").getAsString(),
                    hospitalJson.get("name").getAsString());
            if(!hospitalJson.get("address").isJsonNull()) {
                hospitalBrief.setAddress(hospitalJson.get("address").getAsString());
            }
            hospitals.add(hospitalBrief);
        }
        return hospitals;
    }

    public List<Province> getPList(XmlResourceParser parser) {
        if (pList == null) {
            provinceParser(parser);
        }
        return this.pList;
    }

    public void provinceParser(XmlResourceParser parser) {
        List<City> cList = null;
        List<District> dList = null;

        Province province = null;
        City city = null;
        District district;

        try {
            int type = parser.getEventType();
            while(type != 1) {
                String tag = parser.getName();
                switch (type) {
                    case XmlResourceParser.START_DOCUMENT:
                        pList = new ArrayList<>();
                        break;
                    case XmlResourceParser.START_TAG:
                        if ("p".equals(tag)) {
                            province = new Province();
                            cList = new ArrayList<>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("p_id".equals(name)) {
                                    province.setId(value);
                                }
                            }
                        }
                        if ("pn".equals(tag)) {
                            province.setName(parser.nextText());
                        }
                        if ("c".equals(tag)) {
                            city = new City();
                            dList = new ArrayList<District>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("c_id".equals(name)) {
                                    if (value.length() == 3) {
                                        value = "0" + value;
                                    }
                                    city.setId(value);
                                }
                            }
                        }
                        if ("cn".equals(tag)) {
                            city.setName(parser.nextText());
                        }
                        if ("d".equals(tag)) {
                            district = new District();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("d_id".equals(name)) {
                                    district.setId(value);
                                }
                            }
                            district.setName(parser.nextText());
                            dList.add(district);
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        if ("c".equals(tag)) {
                            dList.get(0).setName("全部");
                            city.setDistricts(dList);
                            cList.add(city);
                        }
                        if ("p".equals(tag)) {
                            province.setCities(cList);
                            pList.add(province);
                        }
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
