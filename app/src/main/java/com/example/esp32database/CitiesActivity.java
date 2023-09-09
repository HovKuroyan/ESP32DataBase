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

public class CitiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<String> savedList;
    List<String> itemList;
    String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        recyclerView = findViewById(R.id.recyclerViewCities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbarCities);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        if (intent != null) {
            region = intent.getStringExtra("Region");
            itemList = new ArrayList<>();
            // Add your data to the itemList here...
            if (region.equals("Gegharkunik")) {
                itemList.add("Gavar");
                itemList.add("Hacarat");
            } else if (region.equals("Kotayk")) {
                itemList.add("Hrazdan");
            }
        }


        itemAdapter = new ItemAdapter(itemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String item) {
                for (int i = 0; i < itemList.size(); i++) {
                    if (item.equals(itemList.get(i))) {
                        Intent intent = new Intent(CitiesActivity.this, BuildingsActivity.class);
                        intent.putExtra("City", itemList.get(i));
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
                if (region.equals("Gegharkunik")) {
                    if (position == 0) {
                        cityLNum = Sizes.GavarSize * 2;
                    } else if (position == 1) {
                        cityFNum = Sizes.GavarSize * 2 + 1;
                        cityLNum = Sizes.GavarSize * 2 + Sizes.HrazdanSize * 2;
                    }
                } else if (region.equals("Kotayk")) {
                    if (position == 0) {
                        cityFNum = Sizes.GegharkunikSize * 2 + 1;
                        cityLNum = cityFNum + Sizes.HrazdanSize;
                    }
                }

                for (int i = cityFNum; i < cityLNum; i += 2) {
                    savedList.set(i, isChecked ? "true" : "false");
                }
                SharedPreferencesHelper.saveStringList(CitiesActivity.this, savedList);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    protected void onResume() {
        getSavedList();
        if (SharedPreferencesHelper.doesListExist(this)) {
            getSavedList();
            setAllItemsTrue();
            for (int i = 1; i < savedList.size(); i += 2) {
                if (region.equals("Gegharkunik") && i < Sizes.GegharkunikSize * 2) {
                    if (savedList.get(i).equals("false") && i < Sizes.GavarSize * 2) {
                        itemAdapter.setCheckedStateForItem("Gavar", false);
                        i = Sizes.GavarSize * 2 + 1;
                    }
                    if (i > Sizes.GavarSize * 2 - 1 && i < Sizes.GavarSize * 2 + Sizes.HacaratSize * 2 && savedList.get(i).equals("false")) {
                        itemAdapter.setCheckedStateForItem("Hacarat", false);
                        i = Sizes.HacaratSize * 2;
                    }
                } else if (region.equals("Kotayk") && i > Sizes.GegharkunikSize * 2 - 1 && i < Sizes.GegharkunikSize * 2 + Sizes.HrazdanSize * 2) {
                    if (savedList.get(i).equals("false")) {
                        itemAdapter.setCheckedStateForItem("Hrazdan", false);
                        i = Sizes.GegharkunikSize * 2 + Sizes.KortaykSize * 2;
                    }
                }
            }
        }
        super.onResume();
    }

    private void getSavedList() {
        boolean listExists = SharedPreferencesHelper.doesListExist(this);

        if (listExists) {
            savedList = SharedPreferencesHelper.getStringList(this);

        }
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