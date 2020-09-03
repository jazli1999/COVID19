package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bupt.sse.group7.covid19.fragment.NotAvailable;
import com.bupt.sse.group7.covid19.fragment.PatientTrackBlockFragment;
import com.bupt.sse.group7.covid19.fragment.StatusLineFragment;
import com.bupt.sse.group7.covid19.interfaces.IPatientViewCallBack;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.model.Status;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientPageActivity extends AppCompatActivity implements IPatientViewCallBack {
    private PatientPresenter patientPresenter;
    private StatusLineFragment statusLineFragment;
    private PatientTrackBlockFragment patientTrackBlockFragment;
    private NotAvailable notAvailable;
    public Map<String, String> statuses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_main_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        statuses = new HashMap<>();

        statuses.put("1", getResources().getString(R.string.confirmed));
        statuses.put("2", getResources().getString(R.string.mild));
        statuses.put("3", getResources().getString(R.string.severe));
        statuses.put("4", getResources().getString(R.string.dead));
        statuses.put("0", getResources().getString(R.string.cured));

        patientPresenter = PatientPresenter.getInstance();
        patientPresenter.registerCallBack(this);
        patientPresenter.getPatientInfo();
    }

    @Override
    public void onPatientInfoReturned(Patient patient) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //TODO 这里不设置Pstatus的话，在statuLineFragment里面new Adapter会有问题
        //        statusLineFragment = new StatusLineFragment();
        //        FragmentTransaction tranStatus = fragmentManager.beginTransaction();
        //        tranStatus.add(R.id.patient_content, statusLineFragment);
        //        tranStatus.commit();

        if (patient.getTrackPoints().size() > 0) {
            patientTrackBlockFragment = new PatientTrackBlockFragment();
            patientTrackBlockFragment.setId(patient.getId());
            FragmentTransaction trackTran = fragmentManager.beginTransaction();
            trackTran.add(R.id.patient_content, patientTrackBlockFragment);
            trackTran.commit();
        }
        else {
            notAvailable = new NotAvailable();
            notAvailable.setTitle("ta的轨迹");
            FragmentTransaction notTran = fragmentManager.beginTransaction();
            notTran.add(R.id.patient_content, notAvailable);
            notTran.commit();
        }

        List<Status> pStatus = patient.getStatuses();
        String desc = MessageFormat.format("{0}  |  {1}",
                statuses.get(patient.getStatus()),
                patient.getH_name());

        ((TextView)this.findViewById(R.id.patient_name)).setText(patient.getUsername());
        ((TextView)this.findViewById(R.id.patient_desc)).setText(desc);
        // statusLineFragment.setpStatus(pStatus);
    }
}