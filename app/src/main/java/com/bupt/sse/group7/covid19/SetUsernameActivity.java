package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SetUsernameActivity extends AppCompatActivity {
    private CardView submit;
    private EditText usernameView;
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_patient_name);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Bundle bundle = this.getIntent().getExtras();
        this.id = bundle.getInt("id");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();
    }

    private void initView() {
        submit = findViewById(R.id.submit_username);
        usernameView = findViewById(R.id.set_username);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUsername();
            }
        });
    }

    private void submitUsername() {
        String username = usernameView.getText().toString();

        JsonObject param = new JsonObject();
        param.add("id", new JsonPrimitive(this.id));
        param.add("username", new JsonPrimitive(username));
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    DBConnector.setUsername(param);
                }
            }
        ).start();
        CurrentUser.setId(this.id);
        CurrentUser.setLabel("patient");
        finish();
        Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
    }
}
