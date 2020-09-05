package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
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
    private static final String TAG = "PatientTrackFragment";
    private View view;

    int mp_id;
    MapView mapView;
    BaiduMap baiduMap;
    BitmapDescriptor bitmap;
    private DrawMarker drawMarker;
    private List<JsonArray> tracklist=new ArrayList<>();
    //定位
    private GeoCoder mCoder;
    private final float mZoom=15.0f;
    private ImageView locationIv;
    private LatLng initialLoc;



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
        locationIv=view.findViewById(R.id.locationIv);
        locationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"locationTvOnClicked");
                LatLng latLng=new LatLng(initialLoc.latitude,initialLoc.longitude);
                Log.i(TAG,"longitude:"+initialLoc.latitude+"   lantitude:"+initialLoc.longitude);
                MapStatus.Builder builder=new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

        //marker图标
       // bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        drawMarker=new DrawMarker(baiduMap,getActivity().getApplicationContext());
        //drawMarker.drawAllRough(tracklist);
        initData(mp_id);
       drawMarker.drawAllDetailWithoutDes(tracklist);
        //drawMarker.drawAllRough(tracklist);
        initLocation();
        //TODO 这里改成和市区选择对应
        mCoder.geocode(new GeoCodeOption()
        .city("北京")
        .address("海淀区"));

    }

    private void initData(int p_id){
        Thread thread=getTrackInfo(p_id);
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    //初始化定位
    private void initLocation() {
        mCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (null != geoCodeResult && null != geoCodeResult.getLocation()) {
                    if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        //没有检索到结果
                        Log.i(TAG, "onGetGeoCodeResult没有检索到结果");
                        return;
                    } else {
                        //定位到选择的区域
                        double latitude = geoCodeResult.getLocation().latitude;
                        double longitude = geoCodeResult.getLocation().longitude;
                        initialLoc=new LatLng(latitude,longitude);
                        Log.i(TAG, "onGetGeoCodeResult:latitude " + latitude + " longitude: " + longitude);
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(initialLoc).zoom(mZoom);
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                    }
                }

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }

        };
        mCoder.setOnGetGeoCodeResultListener(listener);
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
