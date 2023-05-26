package com.example.esp32database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeveloperActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private TextView textViewLogin;
    private CheckBox checkBoxAdmin, checkBoxAlarm;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    public void onBackPressed() {
        //ignored
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        checkBoxAdmin = findViewById(R.id.checkBoxAdmin);
        checkBoxAlarm = findViewById(R.id.checkBoxAlarm);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this); // Initialize Firebase
        db = FirebaseFirestore.getInstance(); // Get Firestore instance


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform registration operation
                registerUser();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open login activity
                finish();
            }
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        boolean isAdmin = checkBoxAdmin.isChecked();
        boolean isAlarm = checkBoxAlarm.isChecked();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();

                            List<String> nameList = new ArrayList<>();
                            nameList.add(name);

                            List<String> uidList = new ArrayList<>();
                            nameList.add(uid);
                            addSchoolNames(nameList, uidList);

                            // Set the initial role as "user"
                            setUserRole(uid, isAdmin ? "admin" : "user", name, password, String.valueOf(isAlarm));

                            Toast.makeText(DeveloperActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Registration failed
                            Toast.makeText(DeveloperActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void addSchoolNames(List<String> schoolNames, List<String> uids) {
        CollectionReference schoolsCollection = db.collection("schools");

        if (schoolNames.size() != uids.size()) {
            return;
        }

        for (int i = 0; i < schoolNames.size(); i++) {
            String schoolName = schoolNames.get(i);
            String uid = uids.get(i);

            DocumentReference documentReference = schoolsCollection.document(uid);

            Map<String, Object> schoolData = new HashMap<>();
            schoolData.put("name", schoolName);
            schoolData.put("uid", uid);

            documentReference.set(schoolData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    private void setUserRole(String uid, String role, String name, String password, String isAlarm) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        DocumentReference userRef = usersRef.document(uid);

        //TODO test part of code here

        User user = new User(uid, role, name, password, isAlarm);

        userRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User role set successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to set user role
                        Toast.makeText(DeveloperActivity.this, "Failed to set user role.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
