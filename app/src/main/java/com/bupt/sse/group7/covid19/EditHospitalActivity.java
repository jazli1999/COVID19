package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class EditHospitalActivity extends AppCompatActivity {
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_hospital_activity);

        this.bundle = this.getIntent().getExtras();

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                submit();
                return true;
            case R.id.action_update:
                Log.d("lyj", "update_clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.hospital_name_edit)).setText(this.bundle.getString("name"));
        ((EditText) findViewById(R.id.set_mild)).setText(String.valueOf(this.bundle.getInt("mild")));
        ((EditText) findViewById(R.id.set_severe)).setText(String.valueOf(this.bundle.getInt("severe")));
        ((EditText) findViewById(R.id.set_inCharge)).setText(this.bundle.getString("people"));
        ((EditText) findViewById(R.id.set_address)).setText(this.bundle.getString("address"));
        ((EditText) findViewById(R.id.set_tel)).setText(this.bundle.getString("tel"));
    }

    public void submit() {
        JsonObject args = new JsonObject();
        JsonObject info = new JsonObject();

        info.add("address", new JsonPrimitive(((EditText) findViewById(R.id.set_address)).getText().toString()));
        info.add("tel", new JsonPrimitive(((EditText) findViewById(R.id.set_tel)).getText().toString()));
        info.add("contact", new JsonPrimitive(((EditText) findViewById(R.id.set_inCharge)).getText().toString()));
        info.add("mild_left", new JsonPrimitive(((EditText) findViewById(R.id.set_mild)).getText().toString()));
        info.add("severe_left", new JsonPrimitive(((EditText) findViewById(R.id.set_severe)).getText().toString()));

        args.add("id", new JsonPrimitive(this.bundle.getInt("id")));
        args.add("row", info);

        updateData(args);

        Toast.makeText(this, "已提交",Toast.LENGTH_SHORT).show();
        finish();
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
