package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 编辑医院信息页面
 */
public class EditHospitalActivity extends AppCompatActivity {
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hospital);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.hospital_name_edit)).setText(this.bundle.getString("name"));
        ((EditText) findViewById(R.id.set_mild)).setText(this.bundle.getString("mild"));
        ((EditText) findViewById(R.id.set_severe)).setText(this.bundle.getString("severe"));
        ((EditText) findViewById(R.id.set_inCharge)).setText(this.bundle.getString("people"));
        ((EditText) findViewById(R.id.set_address)).setText(this.bundle.getString("address"));
        ((EditText) findViewById(R.id.set_tel)).setText(this.bundle.getString("tel"));

        //初始化物资框为主页显示
        ((EditText) findViewById(R.id.set_n95)).setText(this.bundle.getString("n95"));
        ((EditText) findViewById(R.id.set_surgeon)).setText(this.bundle.getString("surgeon"));
        ((EditText) findViewById(R.id.set_ventilator)).setText(this.bundle.getString("ventilator"));
        ((EditText) findViewById(R.id.set_clothe)).setText(this.bundle.getString("clothe"));
        ((EditText) findViewById(R.id.set_glasses)).setText(this.bundle.getString("glasses"));
        ((EditText) findViewById(R.id.set_alcohol)).setText(this.bundle.getString("alcohol"));
        ((EditText) findViewById(R.id.set_pants)).setText(this.bundle.getString("pants"));
    }

    public void submit() {
        JsonObject args = new JsonObject();
        JsonObject info = new JsonObject();


        addValueFromInput(info, "address", R.id.set_address);
        addValueFromInput(info, "tel", R.id.set_tel);
        addValueFromInput(info, "contact", R.id.set_inCharge);
        addValueFromInput(info, "mild_left", R.id.set_mild);
        addValueFromInput(info, "severe_left", R.id.set_severe);

        args.add("id", new JsonPrimitive(this.bundle.getInt("id")));
        args.add("row", info);



        JsonObject args2 = new JsonObject();
        JsonObject supplies = new JsonObject();

        addValueFromInput(supplies, "n95", R.id.set_n95);
        addValueFromInput(supplies, "surgeon", R.id.set_surgeon);
        addValueFromInput(supplies, "ventilator", R.id.set_ventilator);
        addValueFromInput(supplies, "clothe", R.id.set_clothe);
        addValueFromInput(supplies, "glasses", R.id.set_glasses);
        addValueFromInput(supplies, "alcohol", R.id.set_alcohol);
        addValueFromInput(supplies, "pants", R.id.set_pants);

        args2.add("id", new JsonPrimitive(this.bundle.getInt("id")));
        args2.add("row", supplies);

        updateData(args, args2);

        Toast.makeText(this, "已提交",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addValueFromInput(JsonObject obj, String arg, int id) {
        String text = ((EditText)findViewById(id)).getText().toString();
        if (!text.equals("-")) {
            obj.add(arg, new JsonPrimitive(text));
        }
    }

    //第二个参数中包含supplies的所有信息
    private void updateData(JsonObject args,JsonObject args2) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        DBConnector.editHospitalById(args);
                        DBConnector.editSuppliesById(args2);
                    }
                }
        ).start();
    }
}
