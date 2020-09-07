package com.bupt.sse.group7.covid19.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.SetUsernameActivity;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  认证页面，个人用户认证
 */
public class PatientAuthFragment extends Fragment {
    private static final String TAG = "PatientAuthFragment";
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

        Map<String, String> args = new HashMap<>();
        args.put("no", no);

        getAuthInfo(args);

    }

    private void getAuthInfo(Map<String, String> args) {
        Call<ResponseBody> data = DBConnector.dao.executeGet("getPatientAuthInfo.php",args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    returnedInfo= JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    String tel = patientTelView.getText().toString();
                    checkAuth(tel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getAuthInfoOnFailure");

            }
        });
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
