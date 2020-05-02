package com.bupt.sse.group7.covid19;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class PatientMainPageActivity extends AppCompatActivity {

    private int id;
    private JsonObject patient;
    private JsonArray pStatus;
    private StatusLineFragment statusLineFragment;

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

        this.id = 3;
        initView();
        initData();
        updateView();
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        statusLineFragment = new StatusLineFragment();
        FragmentTransaction tranStatus = fragmentManager.beginTransaction();
        tranStatus.add(R.id.patient_content, statusLineFragment);
        tranStatus.commit();
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
                        DBConnector.getPatientTrackById(args);
                    }
                });
        thread.start();
        return thread;
    }
}
