package com.bupt.sse.group7.covid19;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PatientAuthFragment extends Fragment {
    private AlertDialog.Builder builder;
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
        CardView submit = (CardView) view.findViewById(R.id.patient_auth_submit);
        submit.setOnClickListener(new View.OnClickListener() {
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
        int status = returnedInfo.get("status").getAsInt();
        if (status == 1) {
            if (returnedInfo.get("tel").getAsString().equals(tel)) {
                Toast.makeText(getActivity(), "认证成功", Toast.LENGTH_SHORT).show();
                CurrentUser.setId(returnedInfo.get("p_id").getAsInt());
                CurrentUser.setLabel("patient");

                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "病案号或手机错误", Toast.LENGTH_SHORT).show();
            }
        } else if (status == 0) {
            Toast.makeText(getActivity(), "用户不存在", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this.getActivity(), SetUsernameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id", returnedInfo.get("p_id").getAsInt());
            intent.putExtras(bundle);
            startActivity(intent);
            this.getActivity().finish();
        }
    }

}
