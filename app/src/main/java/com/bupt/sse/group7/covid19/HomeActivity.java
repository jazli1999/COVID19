package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {
    private CardView hospitalCard;
    private CardView authCard;
    private CardView trackCard;
    private CardView pageCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        initView();
    }

    private void initView() {
        pageCard = findViewById(R.id.page_card);
        hospitalCard = findViewById(R.id.hospital_card);
        trackCard = findViewById(R.id.track_card);
        authCard = findViewById(R.id.auth_card);

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
    }


    public void intoHospitalList(View view) {

    }
}
