package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientTrackFragment extends Fragment {
    private View view;

    int mp_id;
    MapView mapView;
    BaiduMap baiduMap;
    private JsonArray track;
    BitmapDescriptor bitmap;
    List<OverlayOptions> optionsList = new ArrayList<>();
    List<LatLng> points=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_patient_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initView() {
        mapView = view.findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //marker图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

        initData(mp_id);
        updateView();
    }

    private void initData(int p_id){
        Thread thread=getTrackInfo(p_id);
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    //get all track by p_id
    private Thread getTrackInfo(int p_id){
        final Map<String,String> args=new HashMap<>();
        args.put("p_id",p_id+"");
        Thread thread=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        track=DBConnector.getPatientTrackById(args);
                    }
                }
        );
        thread.start();
        return thread;
    }

    private void updateView(){
        for(int i=0;i<track.size();i++){
            JsonObject object=track.get(i).getAsJsonObject();
            double curLng= object.get("longitude").getAsDouble();
            double curLan= object.get("latitude").getAsDouble();
            String date=object.get("date_time").getAsString();
            String descrip=object.get("description").getAsString();
            LatLng currLatLng=new LatLng(curLan,curLng);
            //添加文字
            OverlayOptions textOption = new TextOptions()
                    //                    .bgColor(0xAAFFFF00)
                    .fontSize(36)
                    .fontColor(Color.BLACK)
                    .text(date+" "+descrip)
                    .position(currLatLng);
            optionsList.add(textOption);

            //添加Marker
            OverlayOptions option=new MarkerOptions().position(currLatLng).icon(bitmap);
            optionsList.add(option);
            baiduMap.addOverlays(optionsList);


            points.add(currLatLng);
        }
        //添加line
        if(points.size()>1) {
            OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
            baiduMap.addOverlay(ooPolyline);
        }
    }

    public void setMp_id(int mp_id) {
        this.mp_id = mp_id;
    }
}
