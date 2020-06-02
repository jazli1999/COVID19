package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class HomeActivity extends AppCompatActivity {
    private CardView hospitalCard;
    private CardView authCard;
    private CardView trackCard;
    private CardView pageCard;

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
        initView();
    }

    private void initData() {
        Thread thread = getStatistics();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        ((TextView) findViewById(R.id.mild_statistic)).setText(
                    String.valueOf(statistics.get("1").getAsInt() + statistics.get("2").getAsInt()));
        ((TextView) findViewById(R.id.severe_statistic)).setText(statistics.get("3").getAsString());
        ((TextView) findViewById(R.id.dead_statistic)).setText(statistics.get("4").getAsString());
        ((TextView) findViewById(R.id.cured_statistic)).setText(statistics.get("0").getAsString());
    }

    private void initView() {
        pageCard = findViewById(R.id.page_card);
        hospitalCard = findViewById(R.id.hospital_card);
        trackCard = findViewById(R.id.track_card);
        authCard = findViewById(R.id.auth_card);

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
                    if (CurrentUser.getLabel().equals("patient")) {
                        context = PatientMainPageActivity.class;
                    } else {
                        context = HospitalMainPageActivity.class;
                    }
                    Intent intent = new Intent(HomeActivity.this, context);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", CurrentUser.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

}


    public void intoHospitalList(View view) {

    }
}
