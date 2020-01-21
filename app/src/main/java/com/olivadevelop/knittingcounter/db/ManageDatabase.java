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
        SQLiteBDHelper currentDB = new SQLiteBDHelper(ct, DATA_BASE, null, 4);
        if (toRead) {
            // modo lectura
            db = currentDB.getReadableDatabase();
        } else {
            // modo escritura
            db = currentDB.getWritableDatabase();
        }
    }

    public void truncate(String table) {
        db.delete(table, null, null);
    }

    public int count(String table) {
        int id = 0;
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            id = c.getInt(0);
        }
        return id;
    }

    public Cursor select(String table, String[] fields, String orderBy) {
        return select(table, fields, orderBy, null, null);
    }

    public Cursor select(String table, String[] fields, String orderBy, String whereClause, String[] whereArgs) {
        return db.query(table, fields, whereClause, whereArgs, null, null, orderBy, null);
    }

    public long insert(String table, String[] fields, String[] values) {
        ContentValues registro = new ContentValues();
        for (int z = 0; z < fields.length; z++) {
            registro.put(fields[z], values[z]);
        }
        // ejecutamos el registro
        return db.insert(table, null, registro);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    public void closeDB() {
        db.close();
    }
}
