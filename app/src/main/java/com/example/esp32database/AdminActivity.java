package com.example.esp32database;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    private DatabaseReference mDatabase, databaseReference;
    private RecyclerView recyclerView;
    private List<Alarm> alarms;
    private AlarmAdapter alarmAdapter;
    private ProgressBar progressBar;
    static boolean isOn = false;
    AlertDialog.Builder builder;
    private FirebaseAuth firebaseAuth;
    TextView tvSchool;
    boolean[] selectedSchool;
    List<Integer> schoolList = new ArrayList<>();
    boolean isAllSelected = false;
    String[] schoolArray = {"School 1", "School 2", "School 3"};
    FirebaseFirestore db;
    List<String> schoolNames, usersUid;
    Spinner schoolNamesSpinner;
    String uid, logUID;

    @Override
    public void onBackPressed() {
        //ignored
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Button btn = findViewById(R.id.admin_btn);

        schoolNames = new ArrayList<>();

        FirebaseApp.initializeApp(this); // Initialize Firebase
        db = FirebaseFirestore.getInstance(); // Get Firestore instance

        tvSchool = findViewById(R.id.admin_select_schools);
        AppCompatButton btnConfig = findViewById(R.id.admin_btn_config);
        Spinner alarmTypeSpinner = findViewById(R.id.admin_alarm_type_spinner);
        schoolNamesSpinner = findViewById(R.id.school_names_spinner);
        recyclerView = findViewById(R.id.admin_recycler_view);
        progressBar = findViewById(R.id.admin_progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();
        String currentUid = firebaseAuth.getCurrentUser().getUid();


//    databaseReference = FirebaseDatabase.getInstance().getReference("alarms").child(currentUid).child("history");


        alarms = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarms, databaseReference);

        recyclerView.setAdapter(alarmAdapter);


        String[] list = getResources().getStringArray(R.array.alarm_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        alarmTypeSpinner.setAdapter(adapter);
        alarmTypeSpinner.setSelection(0);


        builder = new AlertDialog.Builder(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNothingSelected = true;
                for (boolean i : selectedSchool) {
                    if (i) {
                        isNothingSelected = false;
                        break;
                    }
                }
                if (!isNothingSelected) {
                    isOn = !isOn;
                    if (isOn) {

                        builder.setMessage("Вы точно хотите включить сигнализацию?")
                                .setCancelable(false)
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //turn off views
                                        alarmTypeSpinner.setEnabled(false);
                                        tvSchool.setEnabled(false);
                                        schoolNamesSpinner.setEnabled(false);

                                        setChecked(btn, isOn);
//                                        DataBaseHelper dbHelper = new DataBaseHelper(AdminActivity.this);
//                                        List<Result> res = dbHelper.getResults();

//                                        try {
//                                            SmsManager smgr = SmsManager.getDefault();
//                                            smgr.sendTextMessage(res.get(0).getSendText(), null, res.get(0).getSendText(), null, null);
//                                            Toast.makeText(AdminActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
//                                        } catch (Exception e) {
//                                            Toast.makeText(AdminActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
//                                        }
                                        int pos = 0;
                                        for (int i = 0; i < schoolList.size(); i++) {
                                            String name = schoolArray[schoolList.get(i)];
                                            for (int j = 0; j < schoolNames.size(); j++) {
                                                if (name == schoolNames.get(j)) {
                                                    pos = j;
                                                    break;
                                                }
                                            }
                                            String uid = usersUid.get(pos);
                                            mDatabase = FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("my-alarm");
                                            mDatabase.child("isOn").setValue(isOn);
                                            Alarm al = new Alarm((String) DateFormat.format("hh:mm:ss a", new Date()),
                                                    alarmTypeSpinner.getSelectedItem().toString(), isOn ? "On" : "Off");
                                            alarms.add(al);


                                            for (int f = 0; f < alarms.size() / i + 1; f++) {
                                                FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("history").child(String.valueOf(f)).setValue(alarms.get(f));
                                            }
                                        }

                                        Toast.makeText(AdminActivity.this, "Alarm is turned on", Toast.LENGTH_SHORT).show();
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

                        builder.setMessage("Вы точно хотите выключить сигнализацию?")
                                .setCancelable(false)
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //turn on views
                                        alarmTypeSpinner.setEnabled(true);
                                        tvSchool.setEnabled(true);
                                        schoolNamesSpinner.setEnabled(true);

                                        setChecked(btn, isOn);
//                                        DataBaseHelper dbHelper = new DataBaseHelper(AdminActivity.this);
//                                        List<Result> res = dbHelper.getResults();

                                        //TODO fix message sending function

//                                        try {
//                                            SmsManager smgr = SmsManager.getDefault();
//                                            smgr.sendTextMessage(res.get(0).getSendText(), null, res.get(0).getSendText(), null, null);
//                                            Toast.makeText(AdminActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
//                                        } catch (Exception e) {
//                                            Toast.makeText(AdminActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
//                                        }
                                        int pos = 0;
                                        for (int i = 0; i < schoolList.size(); i++) {
                                            String name = schoolArray[schoolList.get(i)];
                                            for (int j = 0; j < schoolNames.size(); j++) {
                                                if (name == schoolNames.get(j)) {
                                                    pos = j;
                                                    break;
                                                }
                                            }
                                            String uid = usersUid.get(pos);
                                            mDatabase = FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("my-alarm");
                                            mDatabase.child("isOn").setValue(isOn);
                                            Alarm al = new Alarm((String) DateFormat.format("hh:mm:ss a", new Date()),
                                                    alarmTypeSpinner.getSelectedItem().toString(), isOn ? "On" : "Off");
                                            alarms.add(al);
                                            for (int f = 0; f < alarms.size() / i + 1; f++) {
                                                FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("history").child(String.valueOf(f)).setValue(alarms.get(f));
                                            }
                                        }
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
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Please select the schools", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, ConfigActivity.class));
            }
        });

        schoolNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < schoolNames.size(); i++) {
                    if (schoolNames.get(i).equals(selectedName)) {  // This line causes the NullPointerException
                        logUID = usersUid.get(i);
                        break;
                    }
                }
                log(logUID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AdminActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
            }
        });
        tvSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customSwitch();
            }
        });
        readSchoolNames();
    }

    private void log(String uid) {
        databaseReference = FirebaseDatabase.getInstance().getReference("alarms").child(uid).child("history");
        //log
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);

                alarms.clear();
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Alarm alarm = dataSnapshot.getValue(Alarm.class);
                        alarms.add(alarm);
                    }
                } catch (RuntimeException e) {
                    Toast.makeText(AdminActivity.this, "Err", Toast.LENGTH_SHORT).show();
                }
                recyclerView.scrollToPosition(alarmAdapter.getItemCount() - 1);
                alarmAdapter.notifyDataSetChanged();
                alarmAdapter = new AlarmAdapter(alarms, databaseReference);

                recyclerView.setAdapter(alarmAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void customSwitch() {
        isAllSelected = true;


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdminActivity.this);

        builder.setTitle("Select school")
                .setMultiChoiceItems(schoolArray, selectedSchool, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selectedSchool[which] = isChecked; // Update selectedSchool array at the clicked position
                        if (isChecked) {
                            schoolList.add(which);
                            Collections.sort(schoolList);
                        } else {
                            schoolList.remove(Integer.valueOf(which)); // Remove the clicked position from schoolList
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < schoolList.size(); i++) {
                            stringBuilder.append(schoolArray[schoolList.get(i)]);
                            if (i != schoolList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        for (boolean i : selectedSchool) {
                            if (!i) {
                                isAllSelected = false;
                                break;
                            }
                        }
                        if (isAllSelected) {
                            tvSchool.setText("All");
                        } else {
                            tvSchool.setText(stringBuilder.toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Arrays.fill(selectedSchool, false);
                        schoolList.clear();
                        tvSchool.setText("");
                    }
                });

        builder.show();
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

    private void readSchoolNames() {
        usersUid = new ArrayList<>();
        CollectionReference schoolsCollection = db.collection("users");

        schoolsCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String schoolName = documentSnapshot.getString("name");
                            String uid = documentSnapshot.getString("uid");
                            String isAlarm = documentSnapshot.getString("isAlarm");
                            if (Objects.equals(isAlarm, "true")) {
                                usersUid.add(uid);
                                schoolNames.add(schoolName);
                            }
                        }
                        schoolArray = new String[schoolNames.size()];

                        for (int i = 0; i < schoolNames.size(); i++) {
                            schoolArray[i] = schoolNames.get(i);
                        }

                        selectedSchool = new boolean[schoolArray.length];
                        tvSchool.setTextColor(Color.BLACK);

                        Arrays.fill(selectedSchool, true);
                        for (int i = 0; i < schoolArray.length; i++) {
                            schoolList.add(i);
                        }
                        tvSchool.setText("All");

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AdminActivity.this, R.layout.spinner_item, schoolNames);
                        schoolNamesSpinner.setAdapter(adapter2);
                        schoolNamesSpinner.setSelection(0);
                        Toast.makeText(AdminActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving school names", e);
                    }
                });
    }
}