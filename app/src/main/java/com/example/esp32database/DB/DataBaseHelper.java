package com.example.esp32database.DB;



import static com.example.esp32database.DB.MyDatabase.COLUMN_BELL_NUMBER;
import static com.example.esp32database.DB.MyDatabase.COLUMN_ID;
import static com.example.esp32database.DB.MyDatabase.COLUMN_RECEIVE_TEXT;
import static com.example.esp32database.DB.MyDatabase.COLUMN_SEND_TEXT;
import static com.example.esp32database.DB.MyDatabase.COLUMN_STAY_IN;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {

    private MyDatabase myDatabase;

    public DataBaseHelper(Context context) {
        myDatabase = new MyDatabase(context);
    }

    public List<Result> getResults() {
        List<Result> resultList = new ArrayList<>();

        Cursor cursor = myDatabase.getAllResults();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String bellNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BELL_NUMBER));
            String sendText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEND_TEXT));
            String receiveText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIVE_TEXT));
            int stayIn = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STAY_IN));

            Result result = new Result(id, bellNumber, sendText, receiveText, stayIn);
            resultList.add(result);
        }
        cursor.close();

        return resultList;
    }

    public void insertResult(Result result) {
        myDatabase.insertResult(result);
    }

    public void updateResult(Result result) {
        myDatabase.updateResult(result);
    }

    public void deleteResult(int id) {
        myDatabase.deleteResult(id);
    }
}

