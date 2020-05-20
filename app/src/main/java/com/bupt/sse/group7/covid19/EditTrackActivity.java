package com.bupt.sse.group7.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class EditTrackActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    int p_id=4;
    private MapView mapView;
    private BaiduMap baiduMap;
    LatLng currLatLng;
    List<OverlayOptions> optionsList=new ArrayList<>();
    List<LatLng> points=new ArrayList<>();
    OverlayOptions options;
    Marker marker;
    List<Marker> markerList=new ArrayList<>();

    private Button btn_end,btn_confirm;

    //时间选择
    private DatePicker datePickerStart;
    private TimePicker timePickerStart;
    private AlertDialog date_time_picker;
    private Button btn_confirmTime;
    List<String > datelist=new ArrayList<>();

    //将坐标转换为地址
    private Handler mHandler;
    private GeoCoder geoCoder;
    private List<String> addressList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_edit_track);
        //返回
        btn_end=findViewById(R.id.btn_end);
        //确认单个marker
        btn_confirm=findViewById(R.id.btn_confirm);

        mapView=findViewById(R.id.mapView);
        baiduMap=mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //marker图标
        final BitmapDescriptor bitmap= BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);


        //时间选择
        AlertDialog.Builder timebuilder = new AlertDialog.Builder(this);
        View timeView = View .inflate(this, R.layout.dialog_date_time, null);
        datePickerStart =  timeView .findViewById(R.id.date_picker);
        timePickerStart = timeView .findViewById(R.id.time_picker);
        timebuilder.setView(timeView);
        timePickerStart.setIs24HourView(true);
        hideYear(datePickerStart);
        date_time_picker=timebuilder.create();

        btn_confirmTime=timeView.findViewById(R.id.btn_confirmTime);
        btn_confirmTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.i("hcccccc","选择的时间是："+(datePickerStart.getMonth()+1)+":"+datePickerStart.getDayOfMonth()
                        +":"+timePickerStart.getHour()+":"+timePickerStart.getMinute());
                date_time_picker.dismiss();

                //date
                String date=datePickerStart.getYear()+"-";
                if(datePickerStart.getMonth()+1<10) date+="0";
                date+=(datePickerStart.getMonth()+1)+"-";
                if(datePickerStart.getDayOfMonth()<10) date+="0";
                date+=datePickerStart.getDayOfMonth();

                //time
                date+=" ";
                if(timePickerStart.getHour()<10) date+="0";
                date+=timePickerStart.getHour()+":";
                if(timePickerStart.getMinute()<10) date+="0";
                date+=timePickerStart.getMinute()+":00";
                datelist.add(date);
                OverlayOptions textOption = new TextOptions()
                        //                    .bgColor(0xAAFFFF00)
                        .fontSize(36)
                        .fontColor(Color.BLACK)
                        .text(date)
                        .position(currLatLng);
                optionsList.add(textOption);
                baiduMap.addOverlay(textOption);
                if(points.size()>1) {
                    OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                    baiduMap.addOverlay(ooPolyline);
                }
            }
        });

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            //点击地图标点
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.clear();
                currLatLng=latLng;
                double latitude=latLng.latitude;
                double longitude=latLng.longitude;
                Log.i("hccc",latitude+","+longitude);
                //LatLng point=new LatLng(latitude,longitude);
                options=new MarkerOptions().position(latLng).icon(bitmap);
                marker= (Marker) baiduMap.addOverlay(options);
                //加载的是之前确认了的以及当前的marker
                baiduMap.addOverlays(optionsList);

            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });

        //确认一个点
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date_time_picker.show();

                //is_confirmed=true;
                markerList.add(marker);
                optionsList.add(options);
                points.add(currLatLng);
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(currLatLng));





            }
        });
        //结束上报，提交到数据库
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        geoCoder= GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //处理消息
                addressList.add(String.valueOf(msg.obj));
                return true;
            }
        });


    }


    //提交到数据库
    private void submit(){
        JsonObject args=new JsonObject();
        JsonArray jsonArray=new JsonArray();
        // jsonArray.add(info);

        for(int i=0;i<points.size();i++) {
            JsonObject info=new JsonObject();

            info.add("date_time", new JsonPrimitive(datelist.get(i)));
            info.add("longitude", new JsonPrimitive(points.get(i).longitude));
            info.add("latitude", new JsonPrimitive(points.get(i).latitude));
            info.add("location", new JsonPrimitive(addressList.get(i)));
            info.add("p_id", new JsonPrimitive(p_id+""));
            jsonArray.add(info);
        }
        args.add("rows", jsonArray);

        addData(args);

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
    private void hideYear(DatePicker datePicker) {
        //安卓5.0以上的处理
        int daySpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if (daySpinner != null) {
                daySpinner.setVisibility(View.GONE);
            }
        }

//        Field[] datePickerFields = datePicker.getClass().getDeclaredFields();
//        for (Field field : datePickerFields) {
//            // 其中mYearSpinner为DatePicker中为“年”定义的变量名
//            if (field.getName().equals("mYearPicker")
//                    || field.getName().equals("mYearSpinner")) {
//                Log.i("hcccc","myearspinner found");
//                field.setAccessible(true);
//                Object dayPicker = new Object();
//                try {
//                    dayPicker = field.get(datePicker);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//                ((View) dayPicker).setVisibility(View.GONE);
//            }
//        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if(reverseGeoCodeResult==null){
            Log.i("hcccc","reverseGeoCodeResult==null");
            return ;

        }
        if(reverseGeoCodeResult.error!= SearchResult.ERRORNO.NO_ERROR) {
            Log.i("hcccc", String.valueOf(reverseGeoCodeResult.error));
            return ;
        }
        ReverseGeoCodeResult.AddressComponent component=reverseGeoCodeResult.getAddressDetail();

        String address=component.street+component.streetNumber;
        Log.i("hcccc","address:"+address);
        Message message=new Message();
        message.what=1;
        message.obj=address;
        mHandler.sendMessage(message);



    }
}
