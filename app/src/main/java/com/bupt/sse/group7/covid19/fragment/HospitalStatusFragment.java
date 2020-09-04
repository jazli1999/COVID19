package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.Statistics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

/**
 *  医院主页，确诊情况卡片部分
 */
public class HospitalStatusFragment extends Fragment {
    private View view;
    private String severe;
    private String mild;
    private String cured;
    private String dead;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital_status, container, false);
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
        ((TextView) view.findViewById(R.id.mild_view)).setText(mild);
        ((TextView) view.findViewById(R.id.severe_view)).setText(severe);
        ((TextView) view.findViewById(R.id.cured_view)).setText(cured);
        ((TextView) view.findViewById(R.id.dead_view)).setText(dead);
    }

    public void setStatistics(Statistics statistics) {
        this.mild = statistics.getMild() + "";
        this.severe = statistics.getSevere() + "";
        this.cured = statistics.getCured() + "";
        this.dead = statistics.getDead() + "";
    }
}
