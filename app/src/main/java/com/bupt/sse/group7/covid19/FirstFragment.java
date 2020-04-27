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

public class FirstFragment extends Fragment {
    private String hospitalInfo = "";
    private Button receive;
    private TextView textView;

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
            Log.d("before", hospitalInfo);
            textView.setText(hospitalInfo);
            Log.d("after", textView.getContext().toString());
        });
    }

    private void getAllHospitals() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        JsonArray hospitals = DBConnector.getAllHospitals();
                        hospitalInfo = hospitals.get(0).getAsJsonObject().get("name") + "\n" + hospitals.get(1).getAsJsonObject().get("name");
                    }
                }
        ).start();
    }

    private void initView(View view) {
        receive = (Button) view.findViewById(R.id.Receive);
        textView = (TextView) view.findViewById(R.id.textView);
    }

}
