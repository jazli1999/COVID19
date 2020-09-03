package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonObject;
import com.bupt.sse.group7.covid19.utils.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 主页
 */
public class HomeActivity extends AppCompatActivity {
    private CardView hospitalCard;
    private CardView authCard;
    private CardView trackCard;
    private CardView pageCard;
    private TextView mildTv, severeTv, curedTv, deadTv;

    private JsonObject statistics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initData();
        Log.i("hcccc","initViewDown");



    }

    private void initData() {
        Log.i("hcccc","initdata");
        Call<ResponseBody> data=DBConnector.dao.getData("getStatistics.php");
       data.enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               try {
                   statistics=DBConnector.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                   initView();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }

           @Override
           public void onFailure(Call<ResponseBody> call, Throwable t) {

           }
       });
//        Thread thread = getStatistics();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private Thread getStatistics() {
        Thread thread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    statistics = DBConnector.getStatistics().get(0).getAsJsonObject();
                }
            }
        );
        thread.start();
        return thread;
    }

    private void updateStatusView() {
        mildTv.setText(String.valueOf(statistics.get(Constants.CONFIRMED + "").getAsInt()
                            + statistics.get(Constants.MILD + "").getAsInt()));
        severeTv.setText(statistics.get(Constants.SEVERE + "").getAsString());
        deadTv.setText(statistics.get(Constants.DEAD + "").getAsString());
        curedTv.setText(statistics.get(Constants.HEALTHY + "").getAsString());
    }

    private void initView() {
        pageCard = findViewById(R.id.page_card);
        hospitalCard = findViewById(R.id.hospital_card);
        trackCard = findViewById(R.id.track_card);
        authCard = findViewById(R.id.auth_card);
        curedTv = findViewById(R.id.cured_statistic);
        deadTv = findViewById(R.id.dead_statistic);
        severeTv = findViewById(R.id.severe_statistic);
        mildTv = findViewById(R.id.mild_statistic);

        updateStatusView();

        hospitalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HospitalListActivity.class);
                startActivity(intent);
            }
        });

        authCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AuthenticateActivity.class);
                startActivity(intent);
            }
        });

        trackCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShowMapActivity.class);
                startActivity(intent);
            }
        });

        pageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentUser.getLabel().equals("visitor")) {
                    Intent auth = new Intent(HomeActivity.this, AuthenticateActivity.class);
                    startActivity(auth);
                    Toast.makeText(HomeActivity.this, "请先认证", Toast.LENGTH_LONG).show();
                } else {
                    Class context;
                    Intent intent;
                    if (CurrentUser.getLabel().equals("patient")) {
                        context = PatientPageActivity.class;
                        PatientPresenter.getInstance().setPatientId(CurrentUser.getId());
                        intent = new Intent(HomeActivity.this, context);
                    } else {
                        context = HospitalMainPageActivity.class;
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", CurrentUser.getId());
                        intent = new Intent(HomeActivity.this, context);
                        intent.putExtras(bundle);
                    }
                    startActivity(intent);
                }
            }
        });

}


    public void intoHospitalList(View view) {

    }
}
