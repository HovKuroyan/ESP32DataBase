package com.example.esp32database;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esp32database.ChoosingArea.ItemAdapter;
import com.example.esp32database.ChoosingArea.OnItemClickListener;
import com.example.esp32database.ChoosingArea.SharedPreferencesHelper;
import com.example.esp32database.ChoosingArea.Sizes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RegionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<String> savedList;
    private List<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        Toolbar toolbar =  findViewById(R.id.toolbarRegion);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewRegion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        itemList.add("Gegharkunik");
        itemList.add("Kotayk");

        itemAdapter = new ItemAdapter(itemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String item) {
                for (int i = 0; i < itemList.size(); i++) {
                    if (item.equals(itemList.get(i))) {
                        Intent intent = new Intent(RegionActivity.this, CitiesActivity.class);
                        intent.putExtra("Region", itemList.get(i));
                        startActivity(intent);
                    }
                }
            }
        });


        itemAdapter.setOnCheckedChangeListener(new ItemAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckboxChanged(int position, boolean isChecked) {
                getSavedList();
                int cityFNum = 1;
                int cityLNum = 1;
                if (position == 0){
                    cityLNum = Sizes.GegharkunikSize * 2;
                } else if (position == 1) {
                    cityFNum = Sizes.GegharkunikSize * 2 + 1;
                    cityLNum = Sizes.GegharkunikSize * 2 + Sizes.KortaykSize * 2;
                }

                for (int i = cityFNum; i < cityLNum; i += 2) {
                    savedList.set(i, isChecked ? "true" : "false");
                }
                SharedPreferencesHelper.saveStringList(RegionActivity.this, savedList);
            }
        });


        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getSavedList() {
        boolean listExists = SharedPreferencesHelper.doesListExist(this);

        if (listExists) {
            savedList = SharedPreferencesHelper.getStringList(this);

        }
    }

    @Override
    protected void onResume() {
        getSavedList();
        if (SharedPreferencesHelper.doesListExist(this)) {
            getSavedList();
            setAllItemsTrue();
            for (int i = 1; i < savedList.size(); i += 2) {
                if (i < Sizes.GegharkunikSize * 2) {
                    if (savedList.get(i).equals("false")) {
                        itemAdapter.setCheckedStateForItem("Gegharkunik", false);
                        i = Sizes.GegharkunikSize * 2 - 1;
                    }
                } else if (i < Sizes.GegharkunikSize * 2 + Sizes.KortaykSize * 2) {
                    if (savedList.get(i).equals("false")) {
                        itemAdapter.setCheckedStateForItem("Kotayk", false);
                        i = Sizes.GegharkunikSize * 2 + Sizes.KortaykSize * 2;
                    }
                }

            }
        }
        super.onResume();
    }

    private void setAllItemsTrue() {
        for (int i = 0; i < itemList.size(); i++) {
            itemAdapter.setCheckedStateForItem(itemList.get(i), true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
