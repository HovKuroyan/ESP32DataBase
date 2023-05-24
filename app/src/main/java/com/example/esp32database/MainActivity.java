package com.example.esp32database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    TextView loginBtn;
    String email, password;
    CheckBox stayIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail = findViewById(R.id.username_input);
        etPassword = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.loginBtn);
        stayIn = findViewById(R.id.stayIn);
        DatabaseReference accounts = FirebaseDatabase.getInstance().getReference("accounts");
        firebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int stayIn = sharedPreferences.getInt("stayIn", 0);
        if (stayIn == 1){
            loadCredentials();
        }

        accounts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the number of child nodes at the root level
                long count = dataSnapshot.getChildrenCount();
                Log.d("Child Count", "Number of child nodes: " + count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase Error", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email cannot be empty");
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password cannot be empty");
                    etPassword.requestFocus();

                } else loginUser(email, password);
            }
        });
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void loadCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        // Use the retrieved values as needed
        etEmail.setText(savedEmail);
        etPassword.setText(savedPassword);
        loginBtn.performClick();
    }

    private void checkAdminRole(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (user.getRole().equals("admin")) {
                                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                                } else if (user.getRole().equals("user")) {
                                    startActivity(new Intent(MainActivity.this, UserActivity.class));
                                } else {
                                    startActivity(new Intent(MainActivity.this, DeveloperActivity.class));
                                }
                            }
                        } else {
                            // User document does not exist
                            Toast.makeText(MainActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to check admin role
                        Toast.makeText(MainActivity.this, "Failed to check admin role.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {
        email.trim();
        password.trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            checkAdminRole(user.getUid());

                            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (stayIn.isChecked()) {
                                editor.putInt("stayIn", 1);
                                saveCredentials(email, password);
                            } else {
                                editor.putInt("stayIn", 0);
                            }
                        } else {
                            // Login failed
                            Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //ignored
    }


}