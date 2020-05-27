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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class HospitalMainPageActivity extends AppCompatActivity {
    private JsonObject hospital;
    private JsonObject statusNumber;
    private JsonObject supplies;
    private HospitalContactFragment contactFragment;
    private HospitalStatusFragment statusFragment;
    private HospitalSuppliesFragment suppliesFragment;
    private int id;
    private String name;
    private String people;
    private String address;
    private String tel;
    private int mild;
    private int severe;

    private String n95;
    private String surgeon;
    private String ventilator;
    private String clothe;
    private String glasses;
    private String alcohol;
    private String pants;

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

        if (this.supplies != null) {
            this.n95 = supplies.get("n95").getAsString();
            this.surgeon = supplies.get("surgeon").getAsString();
            this.ventilator = supplies.get("ventilator").getAsString();
            this.clothe = supplies.get("clothe").getAsString();
            this.glasses = supplies.get("glasses").getAsString();
            this.alcohol = supplies.get("alcohol").getAsString();
            this.pants = supplies.get("pants").getAsString();
        }

        ((TextView)this.findViewById(R.id.hospital_name)).setText(name);
        ((TextView)this.findViewById(R.id.hospital_desc)).setText(MessageFormat.format("剩余床位  轻症 {0} | 重症 {1}",
                this.mild, this.severe));
        updateHospitalContact();
        updateHospitalStatus();
        if (suppliesFragment != null) {
            updateHospitalSupplies();
        }
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


    private void updateHospitalSupplies(){
        suppliesFragment.setN95(this.n95);
        suppliesFragment.setSurgeon(this.surgeon);
        suppliesFragment.setVentilator(this.ventilator);
        suppliesFragment.setClothe(this.clothe);
        suppliesFragment.setGlasses(this.glasses);
        suppliesFragment.setAlcohol(this.alcohol);
        suppliesFragment.setPants(this.pants);
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
                        JsonArray temp = DBConnector.getSuppliesById(args);
                        if (temp != null && temp.size() > 0) {
                            supplies = temp.get(0).getAsJsonObject();
                        }
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

        if (this.suppliesFragment == null) {
            suppliesFragment = new HospitalSuppliesFragment();
            FragmentTransaction tranSupplies = fragmentManager.beginTransaction();
            tranSupplies.add(R.id.hosp_main_content, suppliesFragment);
            tranSupplies.commit();
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

                    bundle.putString("n95",this.n95);
                    bundle.putString("surgeon",this.surgeon);
                    bundle.putString("ventilator",this.ventilator);
                    bundle.putString("clothe",this.clothe);
                    bundle.putString("glasses",this.glasses);
                    bundle.putString("alcohol",this.alcohol);
                    bundle.putString("pants",this.pants);

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
