package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.Supplies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

/**
 * 医院主页，物资情况卡片部分
 */
public class HospitalSuppliesFragment extends Fragment {
    private View view;

    private String n95;
    private String surgeon;
    private String ventilator;
    private String clothe;
    private String glasses;
    private String alcohol;
    private String pants;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital_supplies, container, false);
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
        ((TextView) view.findViewById(R.id.n95_view)).setText(n95);
        ((TextView) view.findViewById(R.id.surgeon_view)).setText(surgeon);
        ((TextView) view.findViewById(R.id.ventilator_view)).setText(ventilator);
        ((TextView) view.findViewById(R.id.clothe_view)).setText(clothe);
        ((TextView) view.findViewById(R.id.glasses_view)).setText(glasses);
        ((TextView) view.findViewById(R.id.alcohol_view)).setText(alcohol);
        ((TextView) view.findViewById(R.id.pants_view)).setText(pants);
    }

    public void setN95(String n95) {
        this.n95 = n95;
    }

    public void setSupplies(Supplies supplies) {
        this.alcohol = supplies.getAlcohol() + "";
        this.n95 = supplies.getN95() + "";
        this.clothe = supplies.getClothe() + "";
        this.glasses = supplies.getGlasses() + "";
        this.pants = supplies.getPants() + "";
        this.surgeon = supplies.getSurgeon() + "";
        this.ventilator = supplies.getVentilator() + "";
    }

}
