package com.example.esp32database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;


public class ConfigActivity extends AppCompatActivity {
    Button btnSave;
    EditText etText, etNumber;
    String bellNumber, sendText;
    DataBaseHelper dbHelper;
    List<Result> res;
    TextView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        btnSave = findViewById(R.id.btnSave);
        etNumber = findViewById(R.id.etPhone);
        etText = findViewById(R.id.etText);
        logOut = findViewById(R.id.logOut);

        dbHelper = new DataBaseHelper(this);
        res = dbHelper.getResults();

        if (!res.get(0).getBellNumber().equals("")) {
            etNumber.setHint(res.get(0).getBellNumber());
        }
        if (!res.get(0).getSendText().equals("")) {
            etText.setHint(res.get(0).getSendText());
        }

        btnSave.setBackgroundColor(Color.parseColor("#008577"));



        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res = dbHelper.getResults();
                dbHelper.updateResult(new Result(1, res.get(0).getBellNumber(), res.get(0).getSendText(), res.get(0).getReceiveText(), 0));
                startActivity(new Intent(ConfigActivity.this, MainActivity.class));
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                res = dbHelper.getResults();
                bellNumber = etNumber.getText().toString();
                sendText = etText.getText().toString();

                if (bellNumber.equals("") && sendText.equals("")) {
                    Toast.makeText(ConfigActivity.this, "Null", Toast.LENGTH_SHORT).show();
                } else {
                    if (bellNumber.equals("")) {
                        bellNumber = res.get(0).getBellNumber();
                    } else if (sendText.equals("")) {
                        sendText = res.get(0).getSendText();
                    }

                    dbHelper.updateResult(new Result(1, bellNumber, sendText, res.get(0).getReceiveText(), res.get(0).getStayIn()));

                    etNumber.setHint(bellNumber);
                    etText.setHint(sendText);
                    etNumber.setText("");
                    etText.setText("");
                    etText.setEnabled(false);
                    etNumber.setEnabled(false);
                    etText.setEnabled(true);
                    etNumber.setEnabled(true);
                }
            }

        });

    }

}