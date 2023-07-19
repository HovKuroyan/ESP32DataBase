package com.example.esp32database;

import android.util.TypedValue;
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
        Alarm alarm = alarms.get(position);
        holder.tvDateTime.setText(alarm.getDateTime());
        holder.tvAlarmType.setText(alarm.getAlarmType());
        holder.tvSchoolName.setText(alarm.getSchoolName());
        holder.switchState.setText(String.valueOf(alarm.getSwitchState()));

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, holder.itemView.getResources().getDisplayMetrics());
        int marginSecond = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.itemView.getResources().getDisplayMetrics());
        layoutParams.setMargins(marginSecond, margin, marginSecond, margin);
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime;
        TextView tvAlarmType;
        TextView switchState;
        TextView tvSchoolName;

        public ViewHolder(View view) {
            super(view);
            tvDateTime = view.findViewById(R.id.tvDateTime);
            tvAlarmType = view.findViewById(R.id.tvAlarmType);
            tvSchoolName = view.findViewById(R.id.tvSchoolName);
            switchState = view.findViewById(R.id.switchState);
        }
    }
}