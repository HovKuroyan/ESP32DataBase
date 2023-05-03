package com.example.esp32database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private List<Alarm> alarms;
    private DatabaseReference databaseReference;

    public AlarmAdapter(List<Alarm> alarms, DatabaseReference databaseReference) {
        this.alarms = alarms;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 2) {
            Alarm alarm = alarms.get(position - 1);
            holder.tvDateTime.setText(alarm.getDateTime());
            holder.tvAlarmType.setText(alarm.getAlarmType());
            holder.switchState.setText(String.valueOf(alarm.isSwitchState()));
        }else {
            Alarm alarm = new Alarm();
//            holder.tvDateTime.setText(alarm.getDateTime());
//            holder.tvAlarmType.setText(alarm.getAlarmType());
//            holder.switchState.setText(String.valueOf(alarm.isSwitchState()));
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime;
        TextView tvAlarmType;
        TextView switchState;

        public ViewHolder(View view) {
            super(view);
            tvDateTime = view.findViewById(R.id.tvDateTime);
            tvAlarmType = view.findViewById(R.id.tvAlarmType);
            switchState = view.findViewById(R.id.switchState);
        }
    }
}