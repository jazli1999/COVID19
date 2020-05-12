package com.bupt.sse.group7.covid19;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity {
    LocationClient locationClient;
    TextView tv_position;
    StringBuilder pos;
    BaiduMap baiduMap;
    MapView mapView;
    boolean isFirstLoc=true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_show_map);
        tv_position=findViewById(R.id.tv_position);
        mapView=findViewById(R.id.mapView);
        baiduMap=mapView.getMap();

        //开启定位图层
        baiduMap.setMyLocationEnabled(true);

        //声明locationClient类
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        List<String> permissionList = new ArrayList<>();
        //如果没有启动下面权限，就询问用户让用户打开
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this, permissions, 1);
        }
        else {
            requestLocation();
        }


    }

    void requestLocation(){
        //initLocation
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        locationClient.setLocOption(option);
        locationClient.start();

    }
    /*只有同意打开相关权限才可以开启本程序*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            pos=new StringBuilder();
            pos.append("纬度:").append(bdLocation.getLatitude()).append("\n");
            pos.append("经度:").append(bdLocation.getLongitude()).append("\n");
            pos.append("街道:").append(bdLocation.getStreet()).append("\n");
            Log.i("hcccc","pos:"+pos.toString());

            //关联地图和定位圈
            MyLocationData locationData=new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            //设置定位数据
            baiduMap.setMyLocationData(locationData);

            if(isFirstLoc){
                isFirstLoc=false;
                LatLng ll=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatus.Builder builder=new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_position.setText(pos.toString());
                }
            });
        }
    }

    @Override
    protected void  onDestroy(){
        super.onDestroy();
        locationClient.stop();
    }
}
