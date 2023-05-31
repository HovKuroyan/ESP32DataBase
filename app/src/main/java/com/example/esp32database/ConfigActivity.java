package com.example.esp32database;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;


public class ConfigActivity extends AppCompatActivity {
    Button btnSave;
    EditText etNumber;
    String bellNumber, sendText;
    DataBaseHelper dbHelper;
    List<Result> res;
    TextView logOut;
    private TimePicker timePicker;
    private Button scheduleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        btnSave = findViewById(R.id.btnSave);
        etNumber = findViewById(R.id.etPhone);
        logOut = findViewById(R.id.logOut);
        timePicker = findViewById(R.id.timePicker);
        scheduleButton = findViewById(R.id.scheduleButton);

        dbHelper = new DataBaseHelper(this);
        res = dbHelper.getResults();

        if (!res.get(0).getBellNumber().equals("")) {
            etNumber.setHint(res.get(0).getBellNumber());
        }

        btnSave.setBackgroundColor(Color.parseColor("#008577"));
        scheduleButton.setBackgroundColor(Color.parseColor("#008577"));


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
                    etNumber.setText("");
                    etNumber.setEnabled(false);
                    etNumber.setEnabled(true);
                }
            }

        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Вы точно хотите запланировать сигнализацию?")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setAlarm();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle(R.string.app_name);
                alert.show();
            }
        });

    }

    private void setAlarm() {
        int selectedHour = timePicker.getHour();
        int selectedMinute = timePicker.getMinute();

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, selectedHour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, selectedMinute);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Turn on the alarm");

        startActivity(intent);
    }

}