package com.example.esp32database.ChoosingArea;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esp32database.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private List<String> itemList;
    private OnItemClickListener itemClickListener;
    private OnCheckedChangeListener onCheckedChangeListener;
    public boolean[] checkedItems;

    public ItemAdapter(List<String> itemList, OnItemClickListener itemClickListener) {
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
        this.checkedItems = new boolean[itemList.size()];
    }

    public interface OnCheckedChangeListener {
        void onCheckboxChanged(int position, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String item = itemList.get(position);
        holder.itemTextView.setText(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, position, item);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(checkedItems[position]);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedItems[position] = isChecked;
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckboxChanged(position, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Method to get the list of checked positions
    public List<Integer> getCheckedPositions() {
        List<Integer> checkedPositions = new ArrayList<>();
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                checkedPositions.add(i);
            }
        }
        return checkedPositions;
    }

    public void setCheckedStateForItem(String itemText, boolean isChecked) {
        int position = itemList.indexOf(itemText);
        if (position != -1) {
            checkedItems[position] = isChecked;
            notifyItemChanged(position);
        }
    }
}
