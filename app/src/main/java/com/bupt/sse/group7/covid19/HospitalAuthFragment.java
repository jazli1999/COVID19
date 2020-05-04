package com.bupt.sse.group7.covid19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class HospitalAuthFragment extends Fragment {
    private EditText userView;
    private EditText passView;
    private JsonObject returnedInfo;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hospital_auth, container, false);
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
        userView = view.findViewById(R.id.hospital_auth_username);
        passView = view.findViewById(R.id.hospital_auth_password);
        Button button = (Button) view.findViewById(R.id.hospital_submit_auth);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHospitalAuth();
            }
        });
    }

    public void submitHospitalAuth() {
        String username = userView.getText().toString();
        String password = passView.getText().toString();

        Map<String, String> args = new HashMap<>();
        args.put("username", username);

        Thread thread = getAuthInfo(args);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkAuth(password);
    }

    private void checkAuth(String password) {
        if (returnedInfo.get("status").getAsInt() == 1) {
            if (returnedInfo.get("password").getAsString().equals(password)) {
                Toast.makeText(getActivity(), "认证成功", Toast.LENGTH_SHORT).show();
                CurrentUser.setId(returnedInfo.get("h_id").getAsInt());
                CurrentUser.setLabel("hospital");

                //TODO 跳转页面
            } else {
                Toast.makeText(getActivity(), "登录名或密码错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "用户不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private Thread getAuthInfo(Map<String, String> args) {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        returnedInfo = DBConnector.getHospitalAuthInfo(args).get(0).getAsJsonObject();
                    }
                });
        thread.start();
        return thread;
    }
}
