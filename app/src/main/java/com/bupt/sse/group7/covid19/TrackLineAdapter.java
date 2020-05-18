package com.bupt.sse.group7.covid19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TrackLineAdapter extends RecyclerView.Adapter<TrackLineAdapter.TrackHolder> {

    private JsonArray list;

    public TrackLineAdapter(JsonArray list, Context context) {
        this.list = list;
    }

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_line_item, null, false);
        TrackHolder holder = new TrackHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder holder, int position) {
        JsonObject obj = list.get(position).getAsJsonObject();
        String[] dateTime = obj.get("date_time").getAsString().split(" ");
        String location = obj.get("location").getAsString();

        holder.dateView.setText(dateTime[0]);
        holder.timeView.setText(dateTime[1].substring(0, 5));
        holder.locationView.setText(location);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    class TrackHolder extends RecyclerView.ViewHolder {
        TextView dateView;
        TextView timeView;
        TextView locationView;

        public TrackHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.track_date);
            timeView = itemView.findViewById(R.id.track_time);
            locationView = itemView.findViewById(R.id.track_location);
        }
    }
}
