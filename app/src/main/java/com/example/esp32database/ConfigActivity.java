package com.example.esp32database;


import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import com.example.esp32database.DB.DataBaseHelper;
import com.example.esp32database.DB.Result;
import java.util.List;


public class ConfigActivity extends AppCompatActivity {
    private DataBaseHelper dbHelper;
    private List<Result> res;
    private TextView logOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        logOut = findViewById(R.id.logOut);

        Toolbar toolbar =  findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        findViewById(R.id.btnMESAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfigActivity.this, NumberMESAlarm.class));
            }
        });

        findViewById(R.id.btnResponsibleNumbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfigActivity.this, ResponsibleNumbers.class));
            }
        });

        findViewById(R.id.btnScheduleAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfigActivity.this, ScheduleAlarm.class));
            }
        });


        dbHelper = new DataBaseHelper(this);
        res = dbHelper.getResults();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res = dbHelper.getResults();
                dbHelper.updateResult(new Result(1, res.get(0).getBellNumber(), res.get(0).getSendText(), res.get(0).getReceiveText(), 0));
                startActivity(new Intent(ConfigActivity.this, MainActivity.class));
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


