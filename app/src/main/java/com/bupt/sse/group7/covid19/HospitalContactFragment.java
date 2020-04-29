package com.bupt.sse.group7.covid19;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class HospitalContactFragment extends Fragment {
    private View view;
    private String people;
    private String tel;
    private String address;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_contact, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    private void initView() {
        ((TextView) view.findViewById(R.id.hosp_tel)).setText(tel);
        ((TextView) view.findViewById(R.id.hosp_address)).setText(address);
        ((TextView) view.findViewById(R.id.hosp_inCharge)).setText(people);
    }


    public void setPeople(String people) {
        this.people = people;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
