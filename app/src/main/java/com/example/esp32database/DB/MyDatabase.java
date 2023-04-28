package com.example.esp32database.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "results.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_RESULTS = "results";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BELL_NUMBER = "bell_number";
    public static final String COLUMN_SEND_TEXT = "send_text";
    public static final String COLUMN_RECEIVE_TEXT = "receive_text";
    public static final String COLUMN_STAY_IN = "stay_in";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULTS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_BELL_NUMBER + " TEXT," +
                COLUMN_SEND_TEXT + " TEXT," +
                COLUMN_RECEIVE_TEXT + " TEXT," +
                COLUMN_STAY_IN + " INTEGER" +
                ")";
        db.execSQL(CREATE_RESULTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }

    public void insertResult(Result result) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BELL_NUMBER, result.getBellNumber());
        values.put(COLUMN_SEND_TEXT, result.getSendText());
        values.put(COLUMN_RECEIVE_TEXT, result.getReceiveText());
        values.put(COLUMN_STAY_IN, result.getStayIn());

        db.insert(TABLE_RESULTS, null, values);
        db.close();
    }

    public void updateResult(Result result) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BELL_NUMBER, result.getBellNumber());
        values.put(COLUMN_SEND_TEXT, result.getSendText());
        values.put(COLUMN_RECEIVE_TEXT, result.getReceiveText());
        values.put(COLUMN_STAY_IN, result.getStayIn());

        db.update(TABLE_RESULTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(result.getId())});

        db.close();
    }

    public void deleteResult(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RESULTS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor getAllResults() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_BELL_NUMBER,
                COLUMN_SEND_TEXT,
                COLUMN_RECEIVE_TEXT,
                COLUMN_STAY_IN
        };

        return db.query(
                TABLE_RESULTS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }
}
