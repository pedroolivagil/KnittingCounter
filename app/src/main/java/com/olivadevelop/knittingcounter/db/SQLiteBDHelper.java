package com.olivadevelop.knittingcounter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Oliva on 02/03/2015.
 */
public class SQLiteBDHelper extends SQLiteOpenHelper {

    private final String TABLE_RECORDS = "CREATE TABLE IF NOT EXISTS " + ManageDatabase.TABLE_PROJECTS + "(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "creation_date TEXT NOT NULL," +
            "lap INTEGER NOT NULL);";

    public SQLiteBDHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_RECORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ManageDatabase.TABLE_PROJECTS);
        onCreate(db);
    }
}
