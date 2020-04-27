package com.bupt.sse.group7.covid19;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment {
    private String hospitalInfo = "";
    private String firstHospital = "";
    private Button receive;
    private TextView textView;
    private Button first;
    private TextView firstView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        receive.setOnClickListener((v) -> {
            getAllHospitals();
            textView.setText(hospitalInfo);
        });
        first.setOnClickListener((v) -> {
            int c_id = 1;
            getHospitalById(c_id);
            firstView.setText(firstHospital);
        });
    }

    private void getHospitalById(int id) {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(id));
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        JsonObject hospital = DBConnector.getHospitalById(args).get(0).getAsJsonObject();

                        firstHospital = hospital.toString();
                    }
                }
        ).start();
    }

    private void getAllHospitals() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        JsonArray hospitals = DBConnector.getAllHospitals();
                        hospitalInfo = "";

                        for(JsonElement hospital : hospitals) {
                            hospitalInfo += "\n" + hospital.getAsJsonObject().get("name").getAsString();
                        }
                    }
                }
        ).start();
    }

    private void initView(View view) {
        receive = (Button) view.findViewById(R.id.Receive);
        textView = (TextView) view.findViewById(R.id.textView);
        first = (Button) view.findViewById(R.id.getFirst);
        firstView = (TextView) view.findViewById(R.id.firstHospital);
    }

}
