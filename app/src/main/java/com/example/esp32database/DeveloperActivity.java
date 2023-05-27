package com.example.esp32database;

import static android.content.ContentValues.TAG;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeveloperActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister, btnDeleteUser;
    private TextView textViewLogin;
    private CheckBox checkBoxAdmin, checkBoxAlarm;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    Spinner deleteUserSpinner;
    List<String> schoolNames, usersUid;


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
        deleteUserSpinner = findViewById(R.id.deleteUserSpinner);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);

        firebaseAuth = FirebaseAuth.getInstance();


        FirebaseApp.initializeApp(this); // Initialize Firebase
        db = FirebaseFirestore.getInstance(); // Get Firestore instance
        readSchoolNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Вы точно хотите удалить пользователя?")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String deleteUserUID = null;
                                for (int i = 0; i < schoolNames.size(); i++) {
                                    if (schoolNames.get(i).equals(deleteUserSpinner.getSelectedItem().toString())) {
                                        deleteUserUID = usersUid.get(i);
                                        break;
                                    }
                                }
                                deleteUser(deleteUserUID);
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle(R.string.app_name);
                alert.show();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSchoolNames();
                builder.setMessage("Вы точно хотите зарегистрировать пользователя?")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                registerUser();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle(R.string.app_name);
                alert.show();
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

    String login = "";
    String password = "";

    private void deleteUser(String deleteUserUID) {
        if (deleteUserUID != null) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("alarms").child(deleteUserUID);
            ref.removeValue();

            CollectionReference schoolsCollection = db.collection("users");

            schoolsCollection.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String uid = documentSnapshot.getString("uid");
                                if (Objects.equals(uid, deleteUserUID)) {
                                    login = documentSnapshot.getString("login");
                                    password = documentSnapshot.getString("password");
                                }
                            }
                            if (!login.isEmpty() && !password.isEmpty()) {
                                firebaseAuth.signOut();

                                firebaseAuth.signInWithEmailAndPassword(login, password)
                                        .addOnCompleteListener(DeveloperActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                user.delete();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error retrieving school names", e);
                        }
                    });
            db.collection("users").document(deleteUserUID).delete();


        }
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
                            setUserRole(uid, isAdmin ? "admin" : "user", name, password, String.valueOf(isAlarm), email);

                            Toast.makeText(DeveloperActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
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

    private void setUserRole(String uid, String role, String name, String password, String isAlarm, String login) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        DocumentReference userRef = usersRef.document(uid);


        User user = new User(uid, role, name, password, isAlarm, login);

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

    private void readSchoolNames() {
        schoolNames = new ArrayList<>();
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

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(DeveloperActivity.this, R.layout.spinner_item, schoolNames);
                        deleteUserSpinner.setAdapter(adapter2);
                        deleteUserSpinner.setSelection(0);
                        Toast.makeText(DeveloperActivity.this, "Success", Toast.LENGTH_SHORT).show();
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
