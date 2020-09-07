package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.adapter.HospitalListAdapter;
import com.bupt.sse.group7.covid19.interfaces.IHospitalListCallBack;
import com.bupt.sse.group7.covid19.model.City;
import com.bupt.sse.group7.covid19.model.District;
import com.bupt.sse.group7.covid19.model.HospitalBrief;
import com.bupt.sse.group7.covid19.model.Province;
import com.bupt.sse.group7.covid19.presenter.HospitalListPresenter;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;

import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 医院列表页面
 */
public class HospitalListActivity extends AppCompatActivity implements IHospitalListCallBack {
    private static final String TAG = "HospitalListActivity";
    private RecyclerView listView;
    private HospitalListAdapter adapter;
    private LinearLayoutManager layoutManager;

    private HospitalListPresenter presenter = HospitalListPresenter.getInstance();
    private CardView filterButton;
    private Spinner spinnerP, spinnerC, spinnerD;
    private List<Province> provinceList;
    ArrayAdapter<Province> provinceAdapter;
    ArrayAdapter<City> cityAdapter;
    ArrayAdapter<District> districtAdapter;
    private Province province;
    private City city;
    private District district;

    private List<Spinner> spinners = new ArrayList<>();
    private List<ArrayAdapter> adapters = new ArrayList<>();

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
        initView();
        initSpinner();
        presenter.registerCallBack(this);
        Log.d(TAG, "onCreate: " + district.getId());
        presenter.setAreaId(getListAreaId());
        presenter.initData();
    }

    private String getListAreaId() {
        if (city.getDistricts().get(0) == district) {
            return city.getId();
        }
        return district.getId();
    }

    public void intoMainPage(int id) {
        HospitalPresenter.getInstance().setID(id);
        Intent intent = new Intent(this, HospitalMainPageActivity.class);
        startActivity(intent);
    }

    private void initSpinner() {
        provinceList = presenter.getPList(getResources().getXml(R.xml.cities));
        provinceAdapter = new ArrayAdapter<Province>(HospitalListActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                provinceList);
        cityAdapter = new ArrayAdapter<City>(HospitalListActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                provinceList.get(0).getCities());
        districtAdapter = new ArrayAdapter<District>(HospitalListActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                provinceList.get(0).getCities().get(0).getDistricts());

        adapters.add(provinceAdapter);
        adapters.add(cityAdapter);
        adapters.add(districtAdapter);

        province = provinceList.get(0);
        city = province.getCities().get(0);
        district = city.getDistricts().get(0);

        for(int i = 0; i < 3; i++) {
            Spinner spinner = spinners.get(i);
            spinner.setAdapter(adapters.get(i));
            spinner.setSelection(0, true);
        }
        spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province = provinceList.get(position);
                cityAdapter = new ArrayAdapter<City>(HospitalListActivity.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        province.getCities());
                spinnerC.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = province.getCities().get(position);
                districtAdapter = new ArrayAdapter<District>(HospitalListActivity.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        city.getDistricts());
                spinnerD.setAdapter(districtAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district = city.getDistricts().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setAreaId(getListAreaId());
                presenter.initData();
            }
        });
    }

    @Override
    public void onHospitalListChanged(List<HospitalBrief> hospitalList) {
        updateView(hospitalList);
    }

    private void updateView(List<HospitalBrief> hospitals) {
        adapter = new HospitalListAdapter(hospitals, this);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }

    private void initView(){
        filterButton = findViewById(R.id.hosp_list_filter);
        spinnerP = findViewById(R.id.spinner_P);
        spinnerC = findViewById(R.id.spinner_C);
        spinnerD = findViewById(R.id.spinner_D);

        spinners.add(spinnerP);
        spinners.add(spinnerC);
        spinners.add(spinnerD);

        listView = findViewById(R.id.hosp_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

}