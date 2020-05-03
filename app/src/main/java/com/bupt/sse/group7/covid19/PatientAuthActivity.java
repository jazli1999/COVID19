package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PatientAuthActivity extends AppCompatActivity {
    private EditText patientNoView;
    private EditText patientTelView;
    private JsonObject returnedInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_auth);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
    }

    public void submitAuth() {
        String no = patientNoView.getText().toString();
        String tel = patientTelView.getText().toString();

        Log.d("auth", no);
        Log.d("auth", tel);

        Map<String, String> args = new HashMap<>();
        args.put("no", no);

        Thread thread = getAuthInfo(args);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkAuth(tel);
    }

    private void intoPatientPage() {
        Intent intent = new Intent(this, PatientMainPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", CurrentUser.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void checkAuth(String tel) {
        Log.d("auth", returnedInfo.toString());
        if (returnedInfo.get("status").getAsInt() == 1) {
            if (returnedInfo.get("tel").getAsString().equals(tel)) {
                Toast.makeText(this, "认证成功", Toast.LENGTH_SHORT).show();
                CurrentUser.setId(returnedInfo.get("p_id").getAsInt());
                CurrentUser.setLabel("patient");

                intoPatientPage();
            } else {
                Toast.makeText(this, "病案号或手机错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private Thread getAuthInfo(Map<String, String> args) {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        returnedInfo = DBConnector.getPatientAuthInfo(args).get(0).getAsJsonObject();
                    }
                });
        thread.start();
        return thread;
    }

    private void initView() {
        patientNoView = findViewById(R.id.patient_auth_no);
        patientTelView = findViewById(R.id.patient_auth_tel);
        Button button = (Button) findViewById(R.id.patient_submit_auth);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAuth();
            }
        });
    }
}
