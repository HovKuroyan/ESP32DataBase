package com.example.esp32database;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, databaseReference, accounts;
    private RecyclerView recyclerView;
    private List<Alarm> alarms;

    private AlarmAdapter alarmAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton btnConfig = findViewById(R.id.btnConfig);
        SwitchCompat alarmSwitch = findViewById(R.id.mySwitch);
        Spinner alarmTypeSpinner = findViewById(R.id.alarm_type_spinner);
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        List<Result> res = dbHelper.getResults();
        //log
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        accounts = FirebaseDatabase.getInstance().getReference("accounts");
        Account account = new Account("AvagDproc", "avagdproc@mail.ru", "12345678");

        accounts.child("1").setValue(account);

        databaseReference = FirebaseDatabase.getInstance().getReference("alarms").child("alarms-school-1").child("history");
        mDatabase = FirebaseDatabase.getInstance().getReference("alarms").child("alarms-school-1").child("my-alarm");


        alarms = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarms, databaseReference);

        recyclerView.setAdapter(alarmAdapter);


        //check is db empty or stay in is checked
        if (res.isEmpty()) {
            dbHelper.insertResult(new Result(1, "", "", "", 0));
            startActivity(new Intent(this, LoginActivity.class));
        } else if (res.get(0).getStayIn() == 0) {
            startActivity(new Intent(this, LoginActivity.class));
        }


        String[] list = getResources().getStringArray(R.array.alarm_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        alarmTypeSpinner.setAdapter(adapter);
        alarmTypeSpinner.setSelection(0);

        //log
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                alarms.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Alarm alarm = dataSnapshot.getValue(Alarm.class);
                    alarms.add(alarm);
                }
                recyclerView.scrollToPosition(alarmAdapter.getItemCount() - 1);
                alarmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //Switch
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDatabase.child("isOn").setValue(isChecked);

                Alarm al = new Alarm((String) DateFormat.format("hh:mm:ss a", new Date()),
                        alarmTypeSpinner.getSelectedItem().toString(), isChecked ? "On" : "Off");
                alarms.add(al);
                for (int i = 1; i < alarms.size(); i++) {
                    databaseReference.child(String.valueOf(i)).setValue(alarms.get(i));
                }
            }
        });

        //Spinner
        alarmTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedType = parent.getItemAtPosition(position).toString();
//                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                mDatabase.child("type").setValue(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.child("isOn").getValue(Boolean.class);
                if (switchState != null) {
                    alarmSwitch.setChecked(switchState);
                }
                String alarmType = snapshot.child("type").getValue(String.class);
                alarmTypeSpinner.setSelection(getIndex(alarmTypeSpinner, alarmType));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
            }
        });
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }
}
