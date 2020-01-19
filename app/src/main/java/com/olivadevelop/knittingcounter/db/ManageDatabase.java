package com.olivadevelop.knittingcounter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by OlivaDevelop on 13/03/2015.
 */
public class ManageDatabase {

    public static final String DATA_BASE = "projects_database.db";
    public static final String TABLE_PROJECTS = "projects";
    public static final boolean TABLE_READ = true;
    public static final boolean TABLE_WRITE = false;

    private static SQLiteDatabase db;

    public ManageDatabase(Context ct, boolean toRead) {
        // Creamos la base de datos (Context, string, cursorFactory, int))
        SQLiteBDHelper currentDB = new SQLiteBDHelper(ct, DATA_BASE, null, 1);
        if (toRead) {
            // modo lectura
            db = currentDB.getReadableDatabase();
        } else {
            // modo escritura
            db = currentDB.getWritableDatabase();
        }
    }

    public void insert(String table, String[] fields, String[] values) {
        ContentValues registro = new ContentValues();
        for (int z = 0; z < fields.length; z++) {
            registro.put(fields[z], values[z]);
        }
        // ejecutamos el registro
        db.insert(table, null, registro);
        System.out.println("Inserted!");
    }

    public void truncate(String table) {
        db.delete(table, null, null);
    }

    public Cursor select(String table, String[] fields, String orderBy, String where) {
        return db.query(table, fields, null, null, null, null, orderBy, null);
    }

    public void closeDB() {
        db.close();
    }
}
