package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PatientAuthFragment extends Fragment {
    private EditText patientNoView;
    private EditText patientTelView;
    private JsonObject returnedInfo;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView() {
        patientNoView = view.findViewById(R.id.patient_auth_no);
        patientTelView = view.findViewById(R.id.patient_auth_tel);
        Button button = (Button) view.findViewById(R.id.patient_submit_auth);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAuth();
            }
        });
    }

    public void submitAuth() {
        String no = patientNoView.getText().toString();
        String tel = patientTelView.getText().toString();

        Map<String, String> args = new HashMap<>();
        args.put("no", no);

        Thread thread = getAuthInfo(args);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkAuth(tel);
    }

    private Thread getAuthInfo(Map<String, String> args) {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        returnedInfo = DBConnector.getPatientAuthInfo(args).get(0).getAsJsonObject();
                    }
                });
        thread.start();
        return thread;
    }

    private void checkAuth(String tel) {
        if (returnedInfo.get("status").getAsInt() == 1) {
            if (returnedInfo.get("tel").getAsString().equals(tel)) {
                Toast.makeText(getActivity(), "认证成功", Toast.LENGTH_SHORT).show();
                CurrentUser.setId(returnedInfo.get("p_id").getAsInt());
                CurrentUser.setLabel("patient");

                intoHomePage();
            } else {
                Toast.makeText(getActivity(), "病案号或手机错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "用户不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void intoHomePage() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }
}
