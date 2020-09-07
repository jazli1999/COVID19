package com.bupt.sse.group7.covid19;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.DrawMarker;
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

/**
 * 主页 -> 轨迹查看页面
 */
public class ShowMapActivity extends AppCompatActivity {

    private final String TAG="ShowMapActivity";

    private final float mZoom=15.0f;
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

    //定位
    private ImageView locationIv;
    private BDLocation mCurrentLoc;
    private boolean isFirstLoc=true;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;

    private GeoCoder mCoder;

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
                    if(baiduMap.getMapStatus().zoom>mZoom){
                        drawMarker.drawAllDetailWithoutDes(alltracklist);

                    }
                    else {

                        drawMarker.drawAllRoughWithoutDes(alltracklist);

                    }

                }

                else {
                    tracklist=new ArrayList<>();
                    //TODO 这里改成和市区选择对应
                    mCoder.geocode(new GeoCodeOption()
                    .city("北京")
                    .address(district_Sp.getSelectedItem().toString()));

                    for (int j = 0; j < allpatientId.size(); j++) {
                        JsonObject object = allpatientId.get(j).getAsJsonObject();
                        int pid = object.get("p_id").getAsInt();

                        initTrackInfo(pid, tv_start.getText().toString(), end, district_Sp.getSelectedItem().toString());

                    }

                    if(baiduMap.getMapStatus().zoom>mZoom){
                        drawMarker.drawAllDetailWithoutDes(tracklist);

                    }
                    else {

                        drawMarker.drawAllRoughWithoutDes(tracklist);

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

        initMap();

        //点击marker跳转到病人主页
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("hcccc","marker onclicked");
                Bundle bundle=marker.getExtraInfo();
                int p_id=bundle.getInt("p_id");
                PatientPresenter.getInstance().setPatientId(p_id);
                Intent intent=new Intent(ShowMapActivity.this,PatientMainPageActivity.class);
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
                if(zoom>mZoom){
                    if(district_Sp.getSelectedItem().toString().equals("全部")){
                        drawMarker.drawAllDetailWithoutDes(alltracklist);
                    }
                    else {
                        drawMarker.drawAllDetailWithoutDes(tracklist);

                    }

                }
                else {
                    if(district_Sp.getSelectedItem().toString().equals("全部")){
                        drawMarker.drawAllRoughWithoutDes(alltracklist);
                    }
                    else {
                        drawMarker.drawAllRoughWithoutDes(tracklist);

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
                PatientPresenter.getInstance().setPatientId(p_id);
                startActivity(intent);
                return false;
            }
        });


        initPatientInfo();
        drawMarker=new DrawMarker(baiduMap,this);
        drawMarker.drawAllRoughWithoutDes(alltracklist);
        initView();
        initLocationOption();

    }


    private void initView(){
        //初始化定位
        locationIv=findViewById(R.id.locationIv);
        locationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"locationTvOnClicked");
                LatLng latLng=new LatLng(mCurrentLoc.getLatitude(),mCurrentLoc.getLongitude());
                Log.i(TAG,"longitude:"+mCurrentLoc.getLongitude()+"   lantitude:"+mCurrentLoc.getLatitude());
                MapStatus.Builder builder=new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

    }


    private void initMap(){
        mapView=findViewById(R.id.mapView);
        baiduMap=mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);

    }
    /**
     * 初始化定位参数配置
     */
    private void initLocationOption(){
        locationClient = new LocationClient(getApplicationContext());
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        myLocationListener = new MyLocationListener();
//注册监听函数
        locationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();

        //根据选择区域定位
        mCoder= GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener=new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (null != geoCodeResult && null != geoCodeResult.getLocation()) {
                    if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        //没有检索到结果
                        Log.i(TAG,"onGetGeoCodeResult没有检索到结果");
                        return;
                    } else {
                        //定位到选择的区域
                        double latitude = geoCodeResult.getLocation().latitude;
                        double longitude = geoCodeResult.getLocation().longitude;
                        Log.i(TAG,"onGetGeoCodeResult:latitude "+latitude+" longitude: "+longitude);
                        MapStatus.Builder builder=new MapStatus.Builder();
                        builder.target(new LatLng(latitude,longitude)).zoom(mZoom);
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
    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            if(isFirstLoc){
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                Log.i(TAG,"longitude:"+location.getLongitude()+"   lantitude:"+location.getLatitude());
                MapStatus.Builder builder=new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstLoc=false;
            }
            mCurrentLoc=location;
            locationClient.stop();
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                 Log.e(TAG,"GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                 Log.e(TAG,"网络");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation){
                Log.e(TAG,"离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError){
                Log.e(TAG,"服务端网络定位失败,错误代码："+location.getLocType());
            } else if (location.getLocType() == BDLocation.TypeNetWorkException){
                Log.e(TAG,"网络不通导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException){
                Log.e(TAG,"无法获取有效定位依据导致定位失败");
            } else {
                Log.e(TAG,"未知原因，请向百度地图SDK论坛求助，location.getLocType()错误代码："+location.getLocType());
            }

        }
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
                        allpatientId= DBConnector.getTrackIds();

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
