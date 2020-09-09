package com.bupt.sse.group7.covid19.fragment;

import android.util.Log;
import android.view.View;

import com.bupt.sse.group7.covid19.EditTrackActivity;
import com.bupt.sse.group7.covid19.adapter.BusLineRvAdapter;
import com.bupt.sse.group7.covid19.adapter.BusStationsRvAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusFragment extends BusBaseFragment {
    @Override
    void bindEvents() {
        BusFragment _this = this;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchField.getText().toString();

                // TODO 获取公交线路的map，替换fakeData
                Map<String, String> fakeData = new HashMap<>();
                fakeData.put("3路", "1");
                fakeData.put("开发区3路", "2");
                updateBusLineView(fakeData, _this);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("lyj", "公交 " + busLineAdapter.getUid() + " " + startAdapter.getChosenName() + " " + endAdapter.getChosenName());
                // TODO 完成 公交 选择，添加到地图
                ((EditTrackActivity) getActivity()).closeBusDialog();
            }
        });
    }


}