package com.bupt.sse.group7.covid19.adapter;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.fragment.BusBaseFragment;
import com.bupt.sse.group7.covid19.fragment.BusFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BusLineRvAdapter extends BusResultRvAdapter {
    private String uid;

    public BusLineRvAdapter(Map<String, String> list, BusBaseFragment context) {
        super(list, context);
    }

    @Override
    void setItemOnClick(@NonNull Holder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastChosen != null) {
                    lastChosen.backgroundView.setBackgroundColor(v.getResources().getColor(R.color.colorPrimary));
                }
                chosenName = nameList.get(holder.getAdapterPosition());
                uid = map.get(chosenName);
                holder.backgroundView.setBackgroundColor(v.getResources().getColor(R.color.chosen));
                lastChosen = holder;

                // TODO 调用搜索公交，将获取的公交站List赋值给nameList，替换下面的fakeData
                List<String> fakeData = new ArrayList();
                fakeData.add("第一个公交站");
                fakeData.add("第二个公交站");
                fakeData.add("第三个公交站");

                context.updateStations(fakeData, context);
            }
        });
    }

    public String getUid() {
        return uid;
    }
}
