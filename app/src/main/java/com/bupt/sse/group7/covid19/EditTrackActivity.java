package com.bupt.sse.group7.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
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
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.map.PolylineDottedLineType.DOTTED_LINE_SQUARE;

/**
 * 病人打点页面
 * TODO 重复添加信息bug
 * TODO Marker使用最新Layout
 */
public class EditTrackActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    private static final String TAG = "EditTrackActivity";
    int p_id = CurrentUser.getId();
    //int p_id=5;
    private MapView mapView;
    private BaiduMap baiduMap;
    LatLng currLatLng;
    List<OverlayOptions> optionsList = new ArrayList<>();
    List<LatLng> points = new ArrayList<>();
    OverlayOptions options;
    Marker marker;
    List<Marker> markerList = new ArrayList<>();

    private Button btn_cancel;

    //时间选择
    private DatePicker datePickerStart;
    private TimePicker timePickerStart;
    private AlertDialog date_time_picker;
    private AlertDialog bus_picker;
    private CardView btn_confirmTime, btn_confirm, btn_bus;
    List<String> datelist = new ArrayList<>();

    //将坐标转换为地址
    private Handler mHandler;
    private GeoCoder geoCoder;
    private List<String> addressList = new ArrayList<>();
    private List<String> districtList = new ArrayList<>();

    //输入描述
    private String description;
    private EditText et_des;
    private AlertDialog desDialog;
    private List<String> descriptionList = new ArrayList<>();

    //定位
    private ImageView locationIv;
    private BDLocation mCurrentLoc;
    private boolean isFirstLoc = true;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private final float mZoom = 15.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_edit_track);
        //返回

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        initLocation();
        initMap();

        //确认单个marker
        btn_confirm = findViewById(R.id.btn_confirm);

        //取消打点
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baiduMap.clear();
                markerList.clear();
                datelist.clear();
                addressList.clear();
                districtList.clear();
                optionsList.clear();
                points.clear();
                descriptionList.clear();
                Bundle bundle = new Bundle();
                bundle.putInt("id", p_id);
                Intent intent = new Intent(EditTrackActivity.this, PatientMainPageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();


            }
        });


        //marker图标
        final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

        // 公交
        AlertDialog.Builder busBuilder = new AlertDialog.Builder(this);
        View busView = View.inflate(this, R.layout.dialog_bus, null);
        busBuilder.setView(busView);
        bus_picker = busBuilder.create();

        //时间选择
        AlertDialog.Builder timeBuilder = new AlertDialog.Builder(this);
        View timeView = View.inflate(this, R.layout.dialog_date_time, null);
        datePickerStart = timeView.findViewById(R.id.date_picker);
        timePickerStart = timeView.findViewById(R.id.time_picker);
        timeBuilder.setView(timeView);
        timePickerStart.setIs24HourView(true);
        hideYear(datePickerStart);
        date_time_picker = timeBuilder.setCancelable(false).create();


        //description输入框
        AlertDialog.Builder desbuilder = new AlertDialog.Builder(this);
        View desview = View.inflate(this, R.layout.dialog_description, null);
        et_des = desview.findViewById(R.id.et_des);
        desbuilder.setView(desview);
        desDialog = desbuilder.setTitle("请输入相关描述")
                .setCancelable(false)
                .setPositiveButton("完成",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                description = et_des.getText().toString();
                                Log.i("hcccc", "输入的description" + description);
                                descriptionList.add(description);
                                et_des.setText("");

                                //date
                                String date = datePickerStart.getYear() + "-";
                                if (datePickerStart.getMonth() + 1 < 10) date += "0";
                                date += (datePickerStart.getMonth() + 1) + "-";
                                if (datePickerStart.getDayOfMonth() < 10) date += "0";
                                date += datePickerStart.getDayOfMonth();

                                //time
                                date += " ";
                                if (timePickerStart.getHour() < 10) date += "0";
                                date += timePickerStart.getHour() + ":";
                                if (timePickerStart.getMinute() < 10) date += "0";
                                date += timePickerStart.getMinute() + ":00";
                                datelist.add(date);
                                OverlayOptions textOption = new TextOptions()
                                        //                    .bgColor(0xAAFFFF00)
                                        .fontSize(36)
                                        .fontColor(Color.BLACK)
                                        .text(date + " " + description)
                                        .position(currLatLng);
                                optionsList.add(textOption);
                                baiduMap.addOverlay(textOption);
                                if (points.size() > 1) {
                                    OverlayOptions ooPolyline = new PolylineOptions().width(8).dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE).color(0xffff941d).points(points);
                                    baiduMap.addOverlay(ooPolyline);
                                }
                            }
                        }).create();

        btn_confirmTime = timeView.findViewById(R.id.btn_confirmTime);
        btn_confirmTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.i("hcccccc", "选择的时间是：" + (datePickerStart.getMonth() + 1) + "-" + datePickerStart.getDayOfMonth()
                        + " " + timePickerStart.getHour() + ":" + timePickerStart.getMinute());
                date_time_picker.dismiss();
                desDialog.show();
            }
        });

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            //点击地图标点
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.clear();
                currLatLng = latLng;
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                Log.i("hccc", latitude + "," + longitude);
                //LatLng point=new LatLng(latitude,longitude);
                options = new MarkerOptions().position(latLng).icon(bitmap);
                marker = (Marker) baiduMap.addOverlay(options);
                marker.setToTop();
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
                Window dialogWindow = date_time_picker.getWindow();
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                date_time_picker.show();


                //is_confirmed=true;
                markerList.add(marker);
                optionsList.add(options);
                points.add(currLatLng);
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(currLatLng));
            }
        });

        btn_bus = findViewById(R.id.bus_button);
        btn_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Window dialogWindow = bus_picker.getWindow();
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                bus_picker.show();
            }
        });

        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //处理消息
                Bundle bundle = msg.getData();
                districtList.add(bundle.getString("district"));
                addressList.add(bundle.getString("address"));
                return true;
            }
        });
    }

    private void initMap() {
        mapView = findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);

    }

    private void initView() {
        locationIv = findViewById(R.id.locationIv);

    }

    private void initLocation() {
        //定位参数
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
        //初始化定位
        locationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "locationTvOnClicked");
                LatLng latLng = new LatLng(mCurrentLoc.getLatitude(), mCurrentLoc.getLongitude());
                Log.i(TAG, "longitude:" + mCurrentLoc.getLongitude() + "   lantitude:" + mCurrentLoc.getLatitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //完成打点并提交到数据库
                submit();
                Intent intent = new Intent(EditTrackActivity.this, PatientMainPageActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //定位


    //提交到数据库
    private void submit() {
        JsonObject args = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        // jsonArray.add(info);

        if (points.size() == 0)
            return;
        for (int i = 0; i < points.size(); i++) {
            JsonObject info = new JsonObject();

            info.add("date_time", new JsonPrimitive(datelist.get(i)));
            info.add("longitude", new JsonPrimitive(points.get(i).longitude));
            info.add("latitude", new JsonPrimitive(points.get(i).latitude));
            info.add("location", new JsonPrimitive(addressList.get(i)));
            info.add("district", new JsonPrimitive(districtList.get(i)));
            info.add("p_id", new JsonPrimitive(p_id + ""));
            info.add("description", new JsonPrimitive(descriptionList.get(i)));
            jsonArray.add(info);
        }
        args.add("rows", jsonArray);
        addData(args);
    }

    public void closeBusDialog() {
        bus_picker.dismiss();
    }

    private void addData(final JsonObject args) {
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
    protected void onDestroy() {
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
        if (reverseGeoCodeResult == null) {
            Log.i("hcccc", "reverseGeoCodeResult==null");
            return;

        }
        if (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.i("hcccc", String.valueOf(reverseGeoCodeResult.error));
            return;
        }
        ReverseGeoCodeResult.AddressComponent component = reverseGeoCodeResult.getAddressDetail();

        String address = component.street + component.streetNumber;
        String district = component.district;
        Log.i("hcccc", "address:" + address);
        Message message = new Message();
        message.what = 1;
        //message.obj=address;
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        bundle.putString("district", district);
        message.setData(bundle);
        mHandler.sendMessage(message);


    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            if (isFirstLoc) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.i(TAG, "longitude:" + location.getLongitude() + "   lantitude:" + location.getLatitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstLoc = false;
            }
            mCurrentLoc = location;
            locationClient.stop();
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                Log.e(TAG, "GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                Log.e(TAG, "网络");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                Log.e(TAG, "离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Log.e(TAG, "服务端网络定位失败,错误代码：" + location.getLocType());
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Log.e(TAG, "网络不通导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Log.e(TAG, "无法获取有效定位依据导致定位失败");
            } else {
                Log.e(TAG, "未知原因，请向百度地图SDK论坛求助，location.getLocType()错误代码：" + location.getLocType());
            }

        }
    }

}
