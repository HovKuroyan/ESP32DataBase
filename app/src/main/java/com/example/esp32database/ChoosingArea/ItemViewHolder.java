package com.example.esp32database.ChoosingArea;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.esp32database.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView itemTextView;
    public CheckBox checkBox;

    public ItemViewHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checkBox);
        itemTextView = itemView.findViewById(R.id.itemTextView);
    }
}
