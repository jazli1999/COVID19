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

import com.bupt.sse.group7.covid19.interfaces.IHospitalViewCallBack;
import com.bupt.sse.group7.covid19.model.Hospital;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 编辑医院信息页面
 */
public class EditHospitalActivity extends AppCompatActivity implements IHospitalViewCallBack {
    private int id;
    private HospitalPresenter hospitalPresenter = HospitalPresenter.getInstance();

    private TextView nameTv;
    private EditText mildTv, severeTv, inChargeTv, addrTv, n95Tv, surgeonTv, ventTv,
            clotheTv, glassesTv, telTv, alcoholTv, pantsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_hospital_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        hospitalPresenter.registerCallBack(this);
        hospitalPresenter.getHospitalDetails();
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
        nameTv = (TextView) findViewById(R.id.hospital_name_edit);
        mildTv = (EditText) findViewById(R.id.set_mild);
        severeTv = (EditText) findViewById(R.id.set_severe);
        inChargeTv = (EditText) findViewById(R.id.set_inCharge);
        addrTv = (EditText) findViewById(R.id.set_address);
        telTv = (EditText) findViewById(R.id.set_tel);

        //初始化物资框为主页显示
        n95Tv = (EditText) findViewById(R.id.set_n95);
        surgeonTv = (EditText) findViewById(R.id.set_surgeon);
        ventTv = (EditText) findViewById(R.id.set_ventilator);
        clotheTv = (EditText) findViewById(R.id.set_clothe);
        glassesTv = (EditText) findViewById(R.id.set_glasses);
        alcoholTv = (EditText) findViewById(R.id.set_alcohol);
        pantsTv = (EditText) findViewById(R.id.set_pants);
    }

    public void submit() {
        JsonObject args = new JsonObject();
        JsonObject info = new JsonObject();


        addValueFromInput(info, "address", R.id.set_address);
        addValueFromInput(info, "tel", R.id.set_tel);
        addValueFromInput(info, "contact", R.id.set_inCharge);
        addValueFromInput(info, "mild_left", R.id.set_mild);
        addValueFromInput(info, "severe_left", R.id.set_severe);

        args.add("id", new JsonPrimitive(this.id));
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

        args2.add("id", new JsonPrimitive(this.id));
        args2.add("row", supplies);

        hospitalPresenter.updateData(args, args2);

        Toast.makeText(this, "已提交",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addValueFromInput(JsonObject obj, String arg, int id) {
        String text = ((EditText)findViewById(id)).getText().toString();
        if (!text.equals("-")) {
            obj.add(arg, new JsonPrimitive(text));
        }
    }

    @Override
    public void onHospitalInfoReturned(Hospital hospital) {
        id = hospital.getId();
        nameTv.setText(hospital.getName());
        mildTv.setText(hospital.getMildLeft());
        severeTv.setText(hospital.getSevereLeft());
        inChargeTv.setText(hospital.getPeople());
        telTv.setText(hospital.getTel());
        addrTv.setText(hospital.getAddress());
        n95Tv.setText(hospital.getSupplies().getN95());
        surgeonTv.setText(hospital.getSupplies().getSurgeon());
        ventTv.setText(hospital.getSupplies().getVentilator());
        clotheTv.setText(hospital.getSupplies().getClothe());
        alcoholTv.setText(hospital.getSupplies().getAlcohol());
        glassesTv.setText(hospital.getSupplies().getGlasses());
        pantsTv.setText(hospital.getSupplies().getPants());
    }
}
