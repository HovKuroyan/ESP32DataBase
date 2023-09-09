package com.example.esp32database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ResponsibleNumbers extends AppCompatActivity {

    private Button buttonAdd;
    private int phoneNumberCount = 0;
    private LinearLayout numbersContainer;
    private EditText editTextPhoneNumber;
    private NumbersDatabaseHelper databaseHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsibles_numbers);
        buttonAdd = findViewById(R.id.buttonAdd);
        numbersContainer = findViewById(R.id.layoutNumbersContainer);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        databaseHelper = new NumbersDatabaseHelper(this);

        Toolbar toolbar =  findViewById(R.id.toolbarRespNumbers);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        List<String> phoneNumbersList;
        try {
            phoneNumbersList = databaseHelper.getAllPhoneNumbers();
            for (String phoneNumber : phoneNumbersList) {
                addPhoneNumberView(phoneNumber);
            }
        } catch (RuntimeException e) {
        }


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumberCount < 3) {
                    addPhoneNumberView();
                } else {
                    Toast.makeText(ResponsibleNumbers.this, "You can't add more than 3 numbers.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void addPhoneNumberView(String phoneNumber) {
        View phoneNumberView = getLayoutInflater().inflate(R.layout.phone_number_item, null);
        EditText editTextPhoneNumberItem = phoneNumberView.findViewById(R.id.editTextPhoneNumberItem);
        Button buttonRemove = phoneNumberView.findViewById(R.id.buttonRemove);

        editTextPhoneNumberItem.setText(phoneNumber);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numbersContainer.removeView(phoneNumberView);
                phoneNumberCount--;
                databaseHelper.deletePhoneNumber(phoneNumber); // You need to add this method to DatabaseHelper
            }
        });

        numbersContainer.addView(phoneNumberView);
    }

    private void addPhoneNumberView() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        if (!phoneNumber.isEmpty()) {
            long id = databaseHelper.insertPhoneNumber(phoneNumber);
            if (id != -1) {
                phoneNumberCount++;
                addPhoneNumberView(phoneNumber);
                editTextPhoneNumber.setText("");
            } else {
                Toast.makeText(this, "Failed to add phone number.", Toast.LENGTH_SHORT).show();
            }
        }
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

class NumbersDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phone_numbers.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "phone_numbers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";

    public NumbersDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PHONE_NUMBER + " TEXT"
                + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public long insertPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<String> getAllPhoneNumbers() {
        List<String> phoneNumbersList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
                phoneNumbersList.add(phoneNumber);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return phoneNumbersList;
    }

    public void deletePhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_PHONE_NUMBER + "=?", new String[]{phoneNumber});
        db.close();
    }



}