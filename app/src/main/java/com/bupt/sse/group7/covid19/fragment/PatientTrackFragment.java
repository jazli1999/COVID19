package com.bupt.sse.group7.covid19.fragment;

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
import com.baidu.mapapi.map.MapView;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.DrawMarker;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 病人主页 -> 轨迹卡片部分 -> 地图显示
 */
public class PatientTrackFragment extends Fragment {
    private View view;

    int mp_id;
    MapView mapView;
    BaiduMap baiduMap;
    BitmapDescriptor bitmap;
    private DrawMarker drawMarker;
    private List<JsonArray> tracklist=new ArrayList<>();


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
       // bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        drawMarker=new DrawMarker(baiduMap,getActivity().getApplicationContext());
        //drawMarker.drawAllRough(tracklist);
        initData(mp_id);
       drawMarker.drawAllDetailWithoutDes(tracklist);
        //drawMarker.drawAllRough(tracklist);

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
                        tracklist.add(DBConnector.getPatientTrackById(args));
                    }
                }
        );
        thread.start();
        return thread;
    }



    public void setMp_id(int mp_id) {
        this.mp_id = mp_id;
    }
}
