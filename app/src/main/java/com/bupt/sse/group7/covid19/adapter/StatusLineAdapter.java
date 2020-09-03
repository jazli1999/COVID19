package com.bupt.sse.group7.covid19.adapter;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.PatientMainPageActivity;
import com.bupt.sse.group7.covid19.R;
import com.google.gson.JsonArray;
import com.bupt.sse.group7.covid19.utils.Constants;
/**
 * 病人状态轴的 RecyclerView Adapter
 */
public class StatusLineAdapter extends RecyclerView.Adapter<StatusLineAdapter.StatusHolder> {

    private JsonArray list;
    private Context context;

    public StatusLineAdapter(JsonArray list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("adapter", parent.getClass().toString());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_line_item, null, false);
        StatusHolder holder = new StatusHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {
        String[] date = list.get(position).getAsJsonObject().get("day").getAsString().split("-");
        String day = date[1] + "/" + date[2];
        int status = list.get(position).getAsJsonObject().get("status").getAsInt();
        GradientDrawable drawable = (GradientDrawable) holder.dotView.getBackground();

        holder.dateView.setText(day);
        holder.infoView.setText(PatientMainPageActivity.statuses.get(status));
        switch(status) {
            case Constants.HEALTHY:
                drawable.setColor(context.getResources().getColor(R.color.healthy));
                break;
            case Constants.CONFIRMED:
                drawable.setColor(context.getResources().getColor(R.color.confirmed));
                break;
            case Constants.MILD:
                drawable.setColor(context.getResources().getColor(R.color.mild));
                break;
            case Constants.SEVERE:
                drawable.setColor(context.getResources().getColor(R.color.severe));
                break;
            case Constants.DEAD:
                drawable.setColor(context.getResources().getColor(R.color.dead));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class StatusHolder extends RecyclerView.ViewHolder {
        TextView infoView;
        TextView dateView;
        TextView dotView;
        TextView lineBefore;
        TextView lineAfter;

        public StatusHolder(View itemView) {
            super(itemView);
            infoView = itemView.findViewById(R.id.status_info);
            dateView = itemView.findViewById(R.id.status_date);
            dotView = itemView.findViewById(R.id.inner_dot);
            lineBefore = itemView.findViewById(R.id.status_line_before);
            lineAfter = itemView.findViewById(R.id.status_line_after);
        }
    }
}
