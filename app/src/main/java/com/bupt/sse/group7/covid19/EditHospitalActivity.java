package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class EditHospitalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_hospital);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void submit(View view) {
        JsonObject args = new JsonObject();
        JsonObject info = new JsonObject();

        info.add("address", new JsonPrimitive("地址"));
        info.add("tel", new JsonPrimitive("19237265830"));
        info.add("contact", new JsonPrimitive("联系人"));
        info.add("mild_left", new JsonPrimitive("76"));
        info.add("severe_left", new JsonPrimitive("37"));

        args.add("id", new JsonPrimitive("4"));
        args.add("row", info);

        updateData(args);

        Toast.makeText(this, "已提交",Toast.LENGTH_SHORT).show();
    }

    private void updateData(JsonObject args) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        DBConnector.editHospitalById(args);
                    }
                }
        ).start();
    }
}
