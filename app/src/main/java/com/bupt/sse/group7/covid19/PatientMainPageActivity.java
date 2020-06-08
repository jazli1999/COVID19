package com.bupt.sse.group7.covid19;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientMainPageActivity extends AppCompatActivity {

    private int id;
    private JsonObject patient;
    private JsonArray pStatus;
    private StatusLineFragment statusLineFragment;
    private PatientTrackBlock patientTrackBlock;
    private NotAvailable notAvailable;
    private JsonArray tracks;

    public static Map<Integer, String> statuses;
    static {
        statuses = new HashMap<>();

        statuses.put(1, "确诊");
        statuses.put(2, "轻症");
        statuses.put(3, "重症");
        statuses.put(4, "死亡");
        statuses.put(0, "已治愈");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_main_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Bundle bundle = this.getIntent().getExtras();
        this.id = bundle.getInt("id");

        initData();
        initView();
        updateView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_track:
                if(CurrentUser.getLabel().equals("patient") && CurrentUser.getId() == this.id) {
                    Intent intent = new Intent(PatientMainPageActivity.this, EditTrackActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "请先认证本用户账号", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_main, menu);
        return true;
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        statusLineFragment = new StatusLineFragment();
        FragmentTransaction tranStatus = fragmentManager.beginTransaction();
        tranStatus.add(R.id.patient_content, statusLineFragment);
        tranStatus.commit();

        if (this.tracks.size() > 0) {
            patientTrackBlock = new PatientTrackBlock();
            patientTrackBlock.setId(this.id);
            FragmentTransaction trackTran = fragmentManager.beginTransaction();
            trackTran.add(R.id.patient_content, patientTrackBlock);
            trackTran.commit();
        }
        else {
            notAvailable = new NotAvailable();
            notAvailable.setTitle("ta的轨迹");
            FragmentTransaction notTran = fragmentManager.beginTransaction();
            notTran.add(R.id.patient_content, notAvailable);
            notTran.commit();
        }
    }

    private void updateView() {
        String desc = MessageFormat.format("{0}  |  {1}",
                                            statuses.get(patient.get("status").getAsInt()),
                                            patient.get("h_name").getAsString());

        ((TextView)this.findViewById(R.id.patient_name)).setText(patient.get("username").getAsString());
        ((TextView)this.findViewById(R.id.patient_desc)).setText(desc);
        updateStatusView();
    }

    private void updateStatusView() {
        statusLineFragment.setpStatus(pStatus);
    }


    private void initData() {
        Thread thread = getPatientInfo(this.id);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Thread getPatientInfo(int p_id) {
        Map<String, String> args = new HashMap<>();
        args.put("p_id", String.valueOf(p_id));
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        patient = DBConnector.getPatientById(args).get(0).getAsJsonObject();
                        pStatus = DBConnector.getPStatusById(args);
                        tracks = DBConnector.getPatientTrackById(args);
                    }
                });
        thread.start();
        return thread;
    }

    private void addTrackData(final JsonObject args){
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
