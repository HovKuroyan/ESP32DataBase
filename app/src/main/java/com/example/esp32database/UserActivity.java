package com.example.esp32database;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private DatabaseReference mDatabase, databaseReference;
    private RecyclerView recyclerView;
    private List<Alarm> alarms;
    private AlarmAdapter alarmAdapter;
    private ProgressBar progressBar;
    static boolean isOn = false;
    AlertDialog.Builder builder;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
        //ignored
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

//        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
//        int stayIn = sharedPreferences.getInt("stayIn", 0);
//        if (stayIn == 0) {
//            startActivity(new Intent(this, MainActivity.class));
//        }


        Button btn = findViewById(R.id.btn);
        FloatingActionButton btnConfig = findViewById(R.id.btnConfig);
        Spinner alarmTypeSpinner = findViewById(R.id.alarm_type_spinner);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("history");

        mDatabase = FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("my-alarm");

        alarms = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarms, databaseReference);

        recyclerView.setAdapter(alarmAdapter);


        String[] list = getResources().getStringArray(R.array.alarm_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        alarmTypeSpinner.setAdapter(adapter);
        alarmTypeSpinner.setSelection(0);
        builder = new AlertDialog.Builder(this);


        //TODO
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if (isOn) {
                    alarmTypeSpinner.setClickable(false);
                    alarmTypeSpinner.setEnabled(false);
                    builder.setMessage("Вы точно хотите включить сигнализацию?")
                            .setCancelable(false)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    setChecked(btn, isOn);
                                    mDatabase.child("isOn").setValue(isOn);
                                    Alarm al = new Alarm((String) DateFormat.format("hh:mm:ss a", new Date()),
                                            alarmTypeSpinner.getSelectedItem().toString(), isOn ? "On" : "Off");
                                    alarms.add(al);
                                    for (int i = 0; i < alarms.size(); i++) {
                                        databaseReference.child(String.valueOf(i)).setValue(alarms.get(i));
                                    }

                                    //add log to history
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference usersRef = db.collection("history");
                                    DocumentReference userRef = usersRef.document(uid);
                                    userRef.set(al).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to set user role
                                            Toast.makeText(UserActivity.this, "Failed to save log.", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    Toast.makeText(UserActivity.this, "Alarm is turned on", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    isOn = !isOn;
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle(R.string.app_name);
                    alert.show();
                } else {
                    alarmTypeSpinner.setClickable(true);
                    alarmTypeSpinner.setEnabled(true);
                    isOn = false;
                    mDatabase.child("isOn").setValue(isOn);
                    Alarm al = new Alarm((String) DateFormat.format("hh:mm:ss a", new Date()),
                            alarmTypeSpinner.getSelectedItem().toString(), isOn ? "On" : "Off");
                    alarms.add(al);
                    for (int i = 0; i < alarms.size(); i++) {
                        databaseReference.child(String.valueOf(i)).setValue(alarms.get(i));
                    }
                    setChecked(btn, false);

                    //add log to history
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference usersRef = db.collection("history");
                    DocumentReference userRef = usersRef.document(uid);
                    userRef.set(al).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to set user role
                            Toast.makeText(UserActivity.this, "Failed to save log.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

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
                Toast.makeText(UserActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    setChecked(btn, switchState);
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
                startActivity(new Intent(UserActivity.this, ConfigActivity.class));
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

    private void setChecked(Button btn, Boolean isChecked) {
        if (!isChecked) {
            btn.setBackground(getResources().getDrawable(R.drawable.button_on_bg));
            btn.setText("On");
        } else {
            btn.setBackground(getResources().getDrawable(R.drawable.button_off_bg));
            btn.setText("Off");
        }
    }
}