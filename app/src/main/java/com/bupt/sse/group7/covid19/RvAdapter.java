package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.Holder> {
    private JsonArray list;
    private AdapterView.OnItemClickListener onItemClickListener;

    public RvAdapter(JsonArray list) {
        this.list = list;
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
        holder.addressView.setText(list.get(position).getAsJsonObject().get("address").getAsString());
        holder.noView.setText(list.get(position).getAsJsonObject().get("h_id").getAsString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HospitalListActivity)ActivityManager.getInstance().getHLA()).
                        intoMainPage(holder.getAdapterPosition() + 1);
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
