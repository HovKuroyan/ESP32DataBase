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

public class BuildingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    String city;
    List<String> savedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        Toolbar toolbar =  findViewById(R.id.toolbarBuildings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> itemList = new ArrayList<>();

        Intent intent = getIntent();
        city = "";

        getSavedList();
        if (intent != null) {
            city = intent.getStringExtra("City");
            if (city.equals("Gavar")) {
                itemList.add("Avag dproc");
                itemList.add("Eritasardakan 1");
            } else if (city.equals("Hacarat")) {
                itemList.add("Eritasardakan 3");
            }else if (city.equals("Hrazdan")) {
                itemList.add("Eritasardakan 2");
            }
        }


        itemAdapter = new ItemAdapter(itemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String item) {
            }
        });

        itemAdapter.setOnCheckedChangeListener(new ItemAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckboxChanged(int position, boolean isChecked) {
                int cityNum = -1;
                getSavedList();
                if (city.equals("Gavar")) {
                    cityNum = 0;
                } else if (city.equals("Hacarat")) {
                    cityNum = Sizes.GavarSize * 2;
                } else if (city.equals("Hrazdan")) {
                    cityNum = Sizes.GegharkunikSize * 2;
                }
                savedList.set(cityNum + (position * 2) + 1, isChecked ? "true" : "false");
                SharedPreferencesHelper.saveStringList(BuildingsActivity.this, savedList);
            }
        });


        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSavedList();
        int cityFNum = 0;
        int cityLNum = 0;
        if (city.equals("Gavar")) {
            cityFNum = 0;
            cityLNum = Sizes.GavarSize * 2;
        } else if (city.equals("Hacarat")) {
            cityFNum = Sizes.GavarSize * 2 - 1;
            cityLNum = (Sizes.GavarSize + Sizes.HacaratSize) * 2;
        } else if (city.equals("Hrazdan")) {
            cityFNum = Sizes.GegharkunikSize * 2 - 1;
            cityLNum = (Sizes.GegharkunikSize + Sizes.HrazdanSize) * 2;
        }
        for (int i = cityFNum + 1; i < cityLNum; i++) {
            if (savedList.get(i).equals("true")) {
                itemAdapter.setCheckedStateForItem(savedList.get(i - 1), true);
            }
        }

    }

    private void getSavedList() {
        boolean listExists = SharedPreferencesHelper.doesListExist(this);

        if (listExists) {
            savedList = SharedPreferencesHelper.getStringList(this);
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