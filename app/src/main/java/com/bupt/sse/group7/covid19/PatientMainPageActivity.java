package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bupt.sse.group7.covid19.fragment.NotAvailable;
import com.bupt.sse.group7.covid19.fragment.PatientTrackBlockFragment;
import com.bupt.sse.group7.covid19.fragment.StatusLineFragment;
import com.bupt.sse.group7.covid19.interfaces.IPatientViewCallBack;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.model.TrackPoint;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.utils.Constants;

import java.text.MessageFormat;

public class PatientMainPageActivity extends AppCompatActivity implements IPatientViewCallBack {
    private static final String TAG = "PatientMainPageActivity";
    private PatientPresenter patientPresenter;
    private StatusLineFragment statusLineFragment;
    private PatientTrackBlockFragment patientTrackBlockFragment;
    private NotAvailable notAvailable;



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


        patientPresenter = PatientPresenter.getInstance();
        patientPresenter.registerCallBack(this);
        patientPresenter.getPatientInfo();


    }




    @Override
    public void onPatientInfoReturned(Patient patient) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //TODO 这里不设置Pstatus的话，在statuLineFragment里面new Adapter会有问题
        statusLineFragment = new StatusLineFragment();
        statusLineFragment.setList(patient.getStatuses());
        FragmentTransaction tranStatus = fragmentManager.beginTransaction();
        tranStatus.add(R.id.patient_content, statusLineFragment);
        tranStatus.commitAllowingStateLoss();

        if (patient.getTrackPoints().size() > 0) {
            patientTrackBlockFragment = new PatientTrackBlockFragment();
            // TODO 这里改成把patient的track传进去不用再查询了，一层一层的改！
            patientTrackBlockFragment.setId(patient.getId());
            FragmentTransaction trackTran = fragmentManager.beginTransaction();
            trackTran.add(R.id.patient_content, patientTrackBlockFragment);
            trackTran.commitAllowingStateLoss();

        }
        else {
            notAvailable = new NotAvailable();
            notAvailable.setTitle("ta的轨迹");
            FragmentTransaction notTran = fragmentManager.beginTransaction();
            notTran.add(R.id.patient_content, notAvailable);
            notTran.commitAllowingStateLoss();
        }

        String desc = MessageFormat.format("{0}  |  {1}",
                Constants.statuses.get(patient.getStatus()),
                patient.getH_name());

        ((TextView)this.findViewById(R.id.patient_name)).setText(patient.getUsername());
        ((TextView)this.findViewById(R.id.patient_desc)).setText(desc);
    }
}
