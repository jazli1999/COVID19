package com.bupt.sse.group7.covid19.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.HospitalListActivity;
import com.bupt.sse.group7.covid19.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * 医院列表的RecyclerView Adapter，给每个医院信息数据绑定对应的视图
 */
public class HospitalListAdapter extends RecyclerView.Adapter<HospitalListAdapter.Holder> {
    private JsonArray list;
    private AdapterView.OnItemClickListener onItemClickListener;
    private HospitalListActivity context;

    public HospitalListAdapter(JsonArray list, HospitalListActivity context) {
        this.list = list;
        this.context = context;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.nameView.setText(list.get(position).getAsJsonObject().get("name").getAsString());
        JsonElement addrElement = list.get(position).getAsJsonObject().get("address");
        if (!addrElement.isJsonNull()) {
            holder.addressView.setText(addrElement.getAsString());
        }
        else {
            holder.addressView.setText("暂无地址");
        }
        holder.noView.setText(list.get(position).getAsJsonObject().get("h_id").getAsString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.intoMainPage(holder.getAdapterPosition() + 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView addressView;
        TextView noView;

        public Holder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.list_hosp_name);
            addressView = itemView.findViewById(R.id.list_hosp_desc);
            noView = itemView.findViewById(R.id.hosp_no);
        }
    }
}
