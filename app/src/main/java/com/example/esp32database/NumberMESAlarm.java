package com.example.esp32database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;

import java.util.List;

public class NumberMESAlarm extends AppCompatActivity {
    Button btnSave;
    DataBaseHelper dbHelper;
    String numberAlarm, numberMES;
    EditText etMESNumber, etAlarmNumber;
    List<Result> res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_mesalarm);
        btnSave = findViewById(R.id.btnSave);
        etMESNumber = findViewById(R.id.etMESNumber);
        etAlarmNumber = findViewById(R.id.etAlarmNumber);

        Toolbar toolbar =  findViewById(R.id.toolbarNumberMESAlarm);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSave.setBackgroundColor(Color.parseColor("#008577"));

        dbHelper = new DataBaseHelper(this);

        res = dbHelper.getResults();

        if (!res.get(0).getBellNumber().equals("")) {
            etAlarmNumber.setHint(res.get(0).getBellNumber());
        }
        if (!res.get(0).getSendText().equals("")) {
            etMESNumber.setHint(res.get(0).getSendText());
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                res = dbHelper.getResults();
                numberMES = etMESNumber .getText().toString();
                numberAlarm = etAlarmNumber.getText().toString();


                if (numberAlarm.equals("") && numberMES.equals("")) {
                    Toast.makeText(NumberMESAlarm.this, "Null", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberAlarm.equals("")) {
                        numberAlarm = res.get(0).getBellNumber();
                    } else if (numberMES.equals("")) {
                        numberMES = res.get(0).getSendText();
                    }

                    dbHelper.updateResult(new Result(1, numberAlarm, numberMES, res.get(0).getReceiveText(), res.get(0).getStayIn()));

                    etMESNumber.setHint(numberMES);
                    etMESNumber.setText("");
                    etMESNumber.setEnabled(false);
                    etMESNumber.setEnabled(true);

                    etAlarmNumber.setHint(numberAlarm);
                    etAlarmNumber.setText("");
                    etAlarmNumber.setEnabled(false);
                    etAlarmNumber.setEnabled(true);
                }
            }

        });
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