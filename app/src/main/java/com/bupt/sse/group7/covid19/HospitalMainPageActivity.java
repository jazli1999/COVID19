package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private JsonObject statusNumber;
    private HospitalContactFragment contactFragment;
    private HospitalStatusFragment statusFragment;
    private int id;
    private String name;
    private String people;
    private String address;
    private String tel;
    private int mild;
    private int severe;

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

        initView();
        initData();

        updateView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_main, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
        updateView();
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
        this.name = hospital.get("name").getAsString();
        this.address = hospital.get("address").getAsString();
        this.people = hospital.get("contact").getAsString();
        this.tel = hospital.get("tel").getAsString();
        this.mild = hospital.get("mild_left").getAsInt();
        this.severe = hospital.get("severe_left").getAsInt();

        ((TextView)this.findViewById(R.id.hospital_name)).setText(name);
        ((TextView)this.findViewById(R.id.hospital_desc)).setText(MessageFormat.format("剩余床位  轻症 {0} | 重症 {1}",
                this.mild, this.severe));
        updateHospitalContact();
        updateHospitalStatus();
    }

    private void updateHospitalStatus() {
        statusFragment.setMild(statusNumber.get("mild").getAsString());
        statusFragment.setSevere(statusNumber.get("severe").getAsString());
        statusFragment.setCured(statusNumber.get("cured").getAsString());
        statusFragment.setDead(statusNumber.get("dead").getAsString());
    }


    private void updateHospitalContact() {
        contactFragment.setTel(this.tel);
        contactFragment.setAddress(this.address);
        contactFragment.setPeople(this.people);
    }

    private Thread getHospitalInfo(int h_id) {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(h_id));
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        hospital = DBConnector.getHospitalById(args).get(0).getAsJsonObject();
                        statusNumber = DBConnector.getStatusNumberById(args).get(0).getAsJsonObject();
                    }
                }
        );
        thread.start();
        return thread;
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (this.statusFragment == null) {
            statusFragment = new HospitalStatusFragment();
            FragmentTransaction tranStatus = fragmentManager.beginTransaction();
            tranStatus.add(R.id.hosp_main_content, statusFragment);
            tranStatus.commit();
        }

        if (this.contactFragment == null) {
            contactFragment = new HospitalContactFragment();
            FragmentTransaction tran = fragmentManager.beginTransaction();
            tran.add(R.id.hosp_main_content, contactFragment);
            tran.commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if(CurrentUser.getLabel().equals("hospital") && CurrentUser.getId() == this.id) {
                    Intent intent = new Intent(this, EditHospitalActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", this.id);
                    bundle.putString("name", this.name);
                    bundle.putString("address", this.address);
                    bundle.putString("people", this.people);
                    bundle.putString("tel", this.tel);
                    bundle.putInt("mild", this.mild);
                    bundle.putInt("severe", this.severe);

                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请先认证本医院账号", Toast.LENGTH_SHORT).show();
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
