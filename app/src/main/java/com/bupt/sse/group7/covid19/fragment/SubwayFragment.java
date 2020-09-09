package com.bupt.sse.group7.covid19.fragment;

import android.util.Log;
import android.view.View;

import com.bupt.sse.group7.covid19.EditTrackActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubwayFragment extends BusBaseFragment {
    @Override
    void bindEvents() {
        SubwayFragment _this = this;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchField.getText().toString();

                // TODO 获取 地铁 线路的map，替换fakeData
                Map<String, String> fakeData = new HashMap<>();
                fakeData.put("3号线", "1");
                fakeData.put("13号线", "2");
                updateBusLineView(fakeData, _this);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("lyj", "地铁 " + busLineAdapter.getUid() + " " + startAdapter.getChosenName() + " " + endAdapter.getChosenName());
                // TODO 完成公交选择，添加到地图
                ((EditTrackActivity) getActivity()).closeBusDialog();
            }
        });
    }
}
