package com.example.esp32database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    TextView loginBtn;
    String email, password;
    DataBaseHelper dbHelper;
    CheckBox stayIn;
    List<Result> res;
    long count = 0;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.username_input);
        etPassword = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.loginBtn);
        dbHelper = new DataBaseHelper(this);
        stayIn = findViewById(R.id.stayIn);
        DatabaseReference accounts = FirebaseDatabase.getInstance().getReference("accounts");
        firebaseAuth = FirebaseAuth.getInstance();

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

                //TODO write code here
                for (int i = 1; i <= count; i++) {
//                    if (accounts.child(String.valueOf(i)))
                }

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
                            LoginActivity.super.finish();
                        } else {
                            // Login failed
                            Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //ignored
    }

    @Override
    public void finish() {
        startActivity(new Intent(this, MainActivity.class));
        res = dbHelper.getResults();
        if (stayIn.isChecked()) {
            dbHelper.updateResult(new Result(1, res.get(0).getBellNumber(), res.get(0).getSendText(), res.get(0).getReceiveText(), 1));
        } else {
            dbHelper.updateResult(new Result(1, res.get(0).getBellNumber(), res.get(0).getSendText(), res.get(0).getReceiveText(), 2));
        }
    }
}