package com.example.esp32database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText etLogin, etPassword;
    TextView loginBtn;
    String login, password;
    DataBaseHelper dbHelper;
    CheckBox stayIn;
    List<Result> res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLogin = findViewById(R.id.username_input);
        etPassword = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.loginBtn);

        dbHelper = new DataBaseHelper(this);
        stayIn = findViewById(R.id.stayIn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login = etLogin.getText().toString();
                    password = etPassword.getText().toString();
                } catch (RuntimeException e) {
                    Toast.makeText(LoginActivity.this, "Please enter login password", Toast.LENGTH_SHORT).show();
                }
                if (Objects.equals(login, "avagdproc@mail.ru") && password.equals("12345678")) {
                    finish();
                } else Toast.makeText(LoginActivity.this, "Wrong data", Toast.LENGTH_SHORT).show();
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
        }else {
            dbHelper.updateResult(new Result(1, res.get(0).getBellNumber(), res.get(0).getSendText(), res.get(0).getReceiveText(), 2));
        }
    }
}