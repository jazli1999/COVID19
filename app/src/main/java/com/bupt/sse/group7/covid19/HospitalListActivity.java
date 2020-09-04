package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bupt.sse.group7.covid19.adapter.HospitalListAdapter;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonArray;

import java.io.IOException;

/**
 * 医院列表页面
 */
public class HospitalListActivity extends AppCompatActivity {
    private static final String TAG = "HospitalListActivity";
    private JsonArray hospitals;
    private RecyclerView listView;
    private HospitalListAdapter adapter;
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

        initView();
        initData();
    }

    public void intoMainPage(int id) {
        HospitalPresenter.getInstance().setID(id);
        Intent intent = new Intent(this, HospitalMainPageActivity.class);
        startActivity(intent);
    }

    private void initData() {
        Call<ResponseBody> data = DBConnector.dao.getData("getHospitalList.php");
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    hospitals= JsonUtils.parseInfo(response.body().byteStream());
                    updateView(hospitals);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "HospitalListActivityOnFailure");

            }
        });

    }

    private void updateView(JsonArray hospitals) {
        adapter = new HospitalListAdapter(hospitals, this);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }

    private void initView() {
        listView = findViewById(R.id.hosp_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    }

}
