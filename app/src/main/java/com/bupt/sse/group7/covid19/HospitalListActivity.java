package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.adapter.RvAdapter;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;

public class HospitalListActivity extends AppCompatActivity {
    private JsonArray hospitals;
    private RecyclerView listView;
    private RvAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initData();
        initView();
    }

    public void intoMainPage(int id) {
        Intent intent = new Intent(this, HospitalMainPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initData() {
        Thread thread = getHospitalList();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Thread getHospitalList() {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        hospitals = DBConnector.getHospitalList();
                    }
                }
        );
        thread.start();
        return thread;
    }

    private void initView() {
        listView = findViewById(R.id.hosp_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new RvAdapter(hospitals, this);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }

}
