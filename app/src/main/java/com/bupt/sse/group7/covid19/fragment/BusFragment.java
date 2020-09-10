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
                Log.d("lyj", "clicked");
                String keyword = searchField.getText().toString();
                ((EditTrackActivity) getActivity()).setBusFragment(_this);
                ((EditTrackActivity) getActivity()).busService(keyword);
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
