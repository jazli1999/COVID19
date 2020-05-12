package com.bupt.sse.group7.covid19;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Build;
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
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowMapActivity extends AppCompatActivity {


    MapView mapView;
    BaiduMap baiduMap;
    private JsonArray track;
    private List<JsonArray> tracklist=new ArrayList<>();
    private JsonArray allpatientId;
    BitmapDescriptor bitmap;
    List<OverlayOptions> optionsList=new ArrayList<>();
    List<LatLng> points=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_show_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mapView=findViewById(R.id.mapView);
        baiduMap=mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("hcccc","marker onclicked");
                Bundle bundle=marker.getExtraInfo();
                int p_id=bundle.getInt("p_id");
                Intent intent=new Intent(ShowMapActivity.this,PatientMainPageActivity.class);
                intent.putExtra("id",p_id);
                startActivity(intent);
                return false;
            }
        });

        baiduMap.setOnPolylineClickListener(new BaiduMap.OnPolylineClickListener() {
            @Override
            public boolean onPolylineClick(Polyline polyline) {
                Log.i("hcccc","marker onclicked");
                Bundle bundle=polyline.getExtraInfo();
                int p_id=bundle.getInt("p_id");
                Intent intent=new Intent(ShowMapActivity.this, PatientMainPageActivity.class);
                intent.putExtra("id",p_id);
                startActivity(intent);
                return false;
            }
        });
        //marker图标
        bitmap= BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

        initPatientInfo();
        updateView();
    }
    private void initPatientInfo(){
        Thread thread=getAllIds();
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        for(int i=0;i<allpatientId.size();i++) {
            JsonObject object = allpatientId.get(i).getAsJsonObject();
            int pid = object.get("p_id").getAsInt();
            initTrackInfo(pid);

        }
    }


    private void initTrackInfo(int p_id){
        Thread thread=getTrackInfo(p_id);
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
    private Thread getAllIds(){

        Thread thread=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        allpatientId=DBConnector.getTrackIds();

                    }
                }
        );
        thread.start();
        return thread;
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
    //一个人的轨迹
    private void updateView(){
        List<Marker> markerList=new ArrayList<>();
        for(int j=0;j<tracklist.size();j++){
            track=tracklist.get(j);
            Bundle bundle=null;
            for(int i=0;i<track.size();i++){

                JsonObject object=track.get(i).getAsJsonObject();
                int id=object.get("p_id").getAsInt();
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

                Marker marker= (Marker) baiduMap.addOverlay(option);
                bundle=new Bundle();
                bundle.putInt("p_id",id);
                Log.i("hcccc","id:"+id);
                marker.setExtraInfo(bundle);
                //optionsList.add(option);
                // markerList.add(marker);
                points.add(currLatLng);
            }
            //添加line
            if(points.size()>1) {
                OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                Polyline polyline= (Polyline) baiduMap.addOverlay(ooPolyline);
                polyline.setExtraInfo(bundle);
            }
            points=new ArrayList<>();
        }
        baiduMap.addOverlays(optionsList);

    }


    private void addData(final JsonObject args){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        DBConnector.addPatientTrack(args);
                    }
                }
        ).start();
    }
}
