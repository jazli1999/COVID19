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
                ((EditTrackActivity) getActivity()).setBusFragment(_this);
                ((EditTrackActivity) getActivity()).busService(keyword);
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
