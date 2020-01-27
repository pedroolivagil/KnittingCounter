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
            "lap INTEGER NOT NULL," +
            "needle_num INTEGER NOT NULL," +
            "header_img_uri TEXT," +
            "option_header_img INTEGER DEFAULT 0);";

    private final String TABLE_GALLERY = "CREATE TABLE IF NOT EXISTS " + ManageDatabase.TABLE_GALLERY + "(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_project INTEGER," +
            "name TEXT NOT NULL," +
            "creation_date TEXT NOT NULL," +
            "img_uri TEXT," +
            "option_create_img INTEGER DEFAULT 0);";

    SQLiteBDHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_RECORDS);
        db.execSQL(TABLE_GALLERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ManageDatabase.TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + ManageDatabase.TABLE_GALLERY);
        onCreate(db);
    }
}
