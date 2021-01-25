package com.olivadevelop.knittingcounter.db.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.olivadevelop.knittingcounter.db.ManageDatabase;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.Tools;
import com.olivadevelop.knittingcounter.tools.ToolsProject;

import java.io.File;

import static com.olivadevelop.knittingcounter.model.Project.COL_CREATION_DATE;
import static com.olivadevelop.knittingcounter.model.Project.COL_HEADER_IMG_URI;
import static com.olivadevelop.knittingcounter.model.Project.COL_ID;
import static com.olivadevelop.knittingcounter.model.Project.COL_LAP;
import static com.olivadevelop.knittingcounter.model.Project.COL_NAME;
import static com.olivadevelop.knittingcounter.model.Project.COL_NEEDLE_NUM;
import static com.olivadevelop.knittingcounter.model.Project.COL_OPTION_HEADER_IMG;

public class ProjectController {

    private final static ProjectController instance = new ProjectController();

    public static ProjectController getInstance() {
        return instance;
    }

    private static final String[] SELECT_ALL_FIELDS = {COL_ID, COL_NAME, COL_CREATION_DATE, COL_LAP, COL_NEEDLE_NUM, COL_HEADER_IMG_URI, COL_OPTION_HEADER_IMG};

    public Cursor findAll(Context c) {
        ManageDatabase md = new ManageDatabase(c, true);
        return md.select(ManageDatabase.TABLE_PROJECTS, SELECT_ALL_FIELDS, COL_ID + " DESC");
    }

    public Project findByName(Context c, String projectName) {
        if (!Tools.isNotEmpty(projectName)) {
            return null;
        }
        return find(c, "LOWER(" + COL_NAME + ") = ?", new String[]{projectName.trim().toLowerCase()});
    }

    public Project findById(Context c, long projectId) {
        if (projectId < 1) {
            return null;
        }
        return find(c, COL_ID + " = ?", new String[]{String.valueOf(projectId)});
    }

    public Project find(Context c, String whereClause, String[] whereArgs) {
        ManageDatabase md = new ManageDatabase(c, true);
        Cursor cursor = md.select(ManageDatabase.TABLE_PROJECTS, SELECT_ALL_FIELDS, null, whereClause, whereArgs);
        Project projectSelected = null;
        if (cursor.moveToNext()) {
            projectSelected = buildProjectFromCursor(cursor);
        }
        md.closeDB();
        return projectSelected;
    }

    public long create(Context c, Project p) {
        ManageDatabase md = new ManageDatabase(c, false);
        long idNew = md.insert(ManageDatabase.TABLE_PROJECTS,
                new String[]{COL_NAME, COL_CREATION_DATE, COL_LAP, COL_NEEDLE_NUM, COL_HEADER_IMG_URI, COL_OPTION_HEADER_IMG},
                new String[]{p.getName().trim(), p.getCreationDate(), String.valueOf(p.getLap()), String.valueOf(p.getNeedleNum()), p.getHeaderImgUri(), String.valueOf(p.getOptionHeaderImage())}
        );
        md.closeDB();
        return idNew;
    }

    public boolean update(Context c, Project p) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, p.getName());
        cv.put(COL_NEEDLE_NUM, p.getNeedleNum());
        cv.put(COL_LAP, p.getLap());
        cv.put(COL_HEADER_IMG_URI, p.getHeaderImgUri());
        cv.put(COL_OPTION_HEADER_IMG, p.getOptionHeaderImage());
        ManageDatabase md = new ManageDatabase(c, false);
        int affectedRows = md.update(ManageDatabase.TABLE_PROJECTS, cv, COL_ID + " = ?", new String[]{String.valueOf(p.get_id())});
        md.closeDB();
        return affectedRows > 0;
    }

    public boolean delete(Context c, Project p) {
        if (p.getHeaderImgUri() != null && ToolsProject.TAKE_PICTURE == p.getOptionHeaderImage()) {
            File img = new File(p.getHeaderImgUri());
            if (img.delete()) {
                System.out.println("Imagen eliminada");
            }
        }
        ManageDatabase md = new ManageDatabase(c, false);
        int affectedRows = md.delete(ManageDatabase.TABLE_PROJECTS, COL_ID + " = ?", new String[]{String.valueOf(p.get_id())});
        md.closeDB();
        return affectedRows > 0;
    }

    public Project buildProjectFromCursor(Cursor cursor) {
        int colID = cursor.getColumnIndex(COL_ID);
        int colName = cursor.getColumnIndex(COL_NAME);
        int colDate = cursor.getColumnIndex(COL_CREATION_DATE);
        int colLap = cursor.getColumnIndex(COL_LAP);
        int colNeedle = cursor.getColumnIndex(COL_NEEDLE_NUM);
        int colHeaderImg = cursor.getColumnIndex(COL_HEADER_IMG_URI);
        int colOptionHeaderImg = cursor.getColumnIndex(COL_OPTION_HEADER_IMG);

        Project projectSelected = new Project();
        if (colID > -1) {
            projectSelected.set_id(cursor.getInt(colID));
        }
        if (colName > -1) {
            projectSelected.setName(cursor.getString(colName));
        }
        if (colDate > -1) {
            projectSelected.setCreationDate(cursor.getString(colDate));
        }
        if (colLap > -1) {
            projectSelected.setLap(cursor.getInt(colLap));
        }
        if (colNeedle > -1) {
            projectSelected.setNeedleNum(cursor.getFloat(colNeedle));
        }
        if (colHeaderImg > -1) {
            projectSelected.setHeaderImgUri(cursor.getString(colHeaderImg));
        }
        if (colOptionHeaderImg > -1) {
            projectSelected.setOptionHeaderImage(cursor.getInt(colOptionHeaderImg));
        }
        return projectSelected;
    }

    public void addLap(Project p) {
        int lap = p.getLap() + 1;
        if (lap > 9999) {
            lap = 9999;
        }
        p.setLap(lap);
    }

    public void removeLap(Project p) {
        int lap = p.getLap() - 1;
        if (lap < 0) {
            lap = 0;
        }
        p.setLap(lap);
    }
}
