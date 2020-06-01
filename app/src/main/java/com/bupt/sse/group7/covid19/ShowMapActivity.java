package com.bupt.sse.group7.covid19;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowMapActivity extends AppCompatActivity {


    private MapView mapView;
    private BaiduMap baiduMap;
    private List<JsonArray> alltracklist=new ArrayList<>();
    private List<JsonArray> tracklist=new ArrayList<>();

    private JsonArray allpatientId;
    private DrawMarker drawMarker;

    //地区选择
    private Spinner district_Sp;
    private ArrayAdapter district_adapter;
    private Button btn_dis;
    //时间选择
    private TextView tv_start;
    private TextView tv_end;
    String end,seven_ago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_show_map);

        //地区选择
        district_Sp=findViewById(R.id.district_spinner);
        district_adapter=ArrayAdapter.createFromResource(this,R.array.beijing,android.R.layout.simple_spinner_item);
        district_Sp.setAdapter(district_adapter);
        btn_dis=findViewById(R.id.btn_district);
        btn_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(district_Sp.getSelectedItem().toString().equals("全部")){
                    alltracklist=new ArrayList<>();
                    for (int j = 0; j < allpatientId.size(); j++) {
                        JsonObject object = allpatientId.get(j).getAsJsonObject();
                        int pid = object.get("p_id").getAsInt();


                        initTrackInfo(pid, tv_start.getText().toString(), end);

                    }
                    if(baiduMap.getMapStatus().zoom>=15){
                        drawMarker.drawAllDetail(alltracklist);

                    }
                    else {

                        drawMarker.drawAllRough(alltracklist);

                    }

                }

                else {
                    tracklist=new ArrayList<>();

                    for (int j = 0; j < allpatientId.size(); j++) {
                        JsonObject object = allpatientId.get(j).getAsJsonObject();
                        int pid = object.get("p_id").getAsInt();

                        initTrackInfo(pid, tv_start.getText().toString(), end, district_Sp.getSelectedItem().toString());

                    }

                    if(baiduMap.getMapStatus().zoom>=15){
                        drawMarker.drawAllDetail(tracklist);

                    }
                    else {

                        drawMarker.drawAllRough(tracklist);

                    }
                }
            }
        });

        //时间选择
        Calendar calendar=Calendar.getInstance();
        tv_start=findViewById(R.id.tv_start);
        tv_end=findViewById(R.id.tv_end);
        int eYear=calendar.get(Calendar.YEAR);
        int eMonth=calendar.get(Calendar.MONTH);
        int eDay=calendar.get(Calendar.DAY_OF_MONTH);
        String now=eYear+"-"+(eMonth+1)+"-"+ eDay;
        tv_end.setText(now);
        end=getDayAfter(now);
        //七天前的日期
        calendar.add(Calendar.DATE,-7);
        int sYear=calendar.get(Calendar.YEAR);
        int sMonth=calendar.get(Calendar.MONTH);
        int sDay =calendar.get(Calendar.DAY_OF_MONTH);
        seven_ago=sYear+"-"+(sMonth+1)+"-"+sDay;
        tv_start.setText(seven_ago);

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tv_start.setText(year+"-"+(++month)+"-"+dayOfMonth);


                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(ShowMapActivity.this, AlertDialog.THEME_HOLO_LIGHT,listener,sYear,sMonth, sDay);
                dialog.show();
            }
        });
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String day=year+"-"+(month+1)+"-"+dayOfMonth;
                        tv_end.setText(day);
                        end=getDayAfter(day);
                        Log.i("hccccc","end"+end);
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(ShowMapActivity.this, AlertDialog.THEME_HOLO_LIGHT,listener,eYear,eMonth,eDay);
                dialog.show();
            }
        });

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
        //点击marker跳转到病人主页
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
        //缩放地图时marker的变化
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                float zoom=mapStatus.zoom;

                //小于200米
                if(zoom>=15){
                    if(district_Sp.getSelectedItem().toString().equals("全部")){
                        drawMarker.drawAllDetail(alltracklist);
                    }
                    else {
                        drawMarker.drawAllDetail(tracklist);

                    }

                }
                else {
                    if(district_Sp.getSelectedItem().toString().equals("全部")){
                        drawMarker.drawAllRough(alltracklist);
                    }
                    else {
                        drawMarker.drawAllRough(tracklist);

                    }
                }


            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {


            }
        });
        //添加line
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


        initPatientInfo();
        drawMarker=new DrawMarker(baiduMap,this);
        drawMarker.drawAllRough(alltracklist);

    }


    //获取某一天的后一天
    private String getDayAfter(String specifiedDay){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day+1);

        String dayAfter=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayAfter;
    }
    //打开页面时候初始化，获取所有病人的轨迹信息
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
            initTrackInfo(pid,seven_ago,end);

        }
    }


    //根据p_id获取病人的轨迹
    private void initTrackInfo(int p_id,String low,String up){
        Thread thread=getTrackInfo(p_id,low,up);
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    //获取所有病人id
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
    //根据p_id、date、行政区获取病人的轨迹
    private void initTrackInfo(int p_id,String low,String up,String district){
        Thread thread=getTrackInfo(p_id,low,up ,district);
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    //根据时间和区域选择
    private Thread getTrackInfo(int p_id,String low,String up,String district){
        final Map<String,String> args=new HashMap<>();
        args.put("p_id",p_id+"");
        args.put("low",low);
        args.put("up",up);
        args.put("district",district);

        Thread thread=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        JsonArray track=DBConnector.getTrackByDateAndTrack(args);
                        tracklist.add(track);
                    }
                }
        );

        thread.start();
        return thread;
    }


    //get all track by p_id and time
    private Thread getTrackInfo(int p_id,String low,String up){
        final Map<String,String> args=new HashMap<>();

        args.put("p_id",p_id+"");
        args.put("low",low);
        args.put("up",up);
        Thread thread=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        alltracklist.add(DBConnector.getTrackByIdAndDate(args));

                    }
                }
        );
        thread.start();
        return thread;
    }

}
