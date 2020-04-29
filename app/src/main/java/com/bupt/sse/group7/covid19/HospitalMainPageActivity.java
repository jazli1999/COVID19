package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class HospitalMainPageActivity extends AppCompatActivity {
    private JsonObject hospital;
    private HospitalContactFragment contactFragment;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Bundle bundle = this.getIntent().getExtras();
        this.id = bundle.getInt("id");
        Log.d("ID", String.valueOf(id));

        initView();
        initData();
        updateView();
    }

    // TODO call initView()
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_main, menu);
        return true;
    }

    private void initData() {
        Thread thread = getHospitalInfo(this.id);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void updateView() {
        ((TextView)this.findViewById(R.id.hospital_name)).setText(hospital.get("name").getAsString());
        ((TextView)this.findViewById(R.id.hospital_desc)).setText(MessageFormat.format("剩余床位  轻症 {0} | 重症 {1}",
                hospital.get("mild_left").getAsString(),
                hospital.get("severe_left").getAsString()));
        updateHospitalContact();
    }


    private void updateHospitalContact() {
        contactFragment.setTel(hospital.get("tel").getAsString());
        contactFragment.setAddress(hospital.get("address").getAsString());
        contactFragment.setPeople(hospital.get("contact").getAsString());
    }

    private Thread getHospitalInfo(int h_id) {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(h_id));
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        hospital = DBConnector.getHospitalById(args).get(0).getAsJsonObject();
                    }
                }
        );
        thread.start();
        return thread;
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        contactFragment = new HospitalContactFragment();
        FragmentTransaction tran = fragmentManager.beginTransaction();
        tran.add(R.id.hosp_main_content, contactFragment);
        tran.commit();
    }

}
