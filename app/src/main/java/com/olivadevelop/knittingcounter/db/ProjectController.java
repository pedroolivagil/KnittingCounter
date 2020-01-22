package com.olivadevelop.knittingcounter.db;

import android.content.Context;
import android.database.Cursor;

import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.Tools;

import static com.olivadevelop.knittingcounter.model.Project.COL_CREATION_DATE;
import static com.olivadevelop.knittingcounter.model.Project.COL_HEADER_IMG_URI;
import static com.olivadevelop.knittingcounter.model.Project.COL_ID;
import static com.olivadevelop.knittingcounter.model.Project.COL_LAP;
import static com.olivadevelop.knittingcounter.model.Project.COL_NAME;
import static com.olivadevelop.knittingcounter.model.Project.COL_NEEDLE_NUM;

public class ProjectController {

    private final static ProjectController instance = new ProjectController();

    private static final String[] SELECT_ALL_FIELDS = {COL_ID, COL_NAME, COL_CREATION_DATE, COL_LAP, COL_NEEDLE_NUM, COL_HEADER_IMG_URI};

    public static ProjectController getInstance() {
        return instance;
    }

    public Cursor findAllProject(Context c) {
        ManageDatabase md = new ManageDatabase(c, true);
        return md.select(ManageDatabase.TABLE_PROJECTS, SELECT_ALL_FIELDS, COL_ID + " DESC");
    }

    public Project findProjectName(Context c, String projectName) {
        if (!Tools.isNotEmpty(projectName)) {
            return null;
        }
        return findProject(c, "LOWER(" + COL_NAME + ") = ?", new String[]{projectName.trim().toLowerCase()});
    }

    public Project findProject(Context c, String whereClause, String[] whereArgs) {
        ManageDatabase md = new ManageDatabase(c, true);
        Cursor cursor = md.select(ManageDatabase.TABLE_PROJECTS, SELECT_ALL_FIELDS, null, whereClause, whereArgs
        );
        Project projectSelected = buildProjectFromCursor(cursor);
        md.closeDB();
        return projectSelected;
    }

    public long createProject(Context c, Project p) {
        ManageDatabase md = new ManageDatabase(c, false);
        long idNew = md.insert(ManageDatabase.TABLE_PROJECTS,
                new String[]{COL_NAME, COL_CREATION_DATE, COL_LAP, COL_NEEDLE_NUM, COL_HEADER_IMG_URI},
                new String[]{p.getName().trim(), p.getCreationDate(), String.valueOf(p.getLap()), String.valueOf(p.getNeedleNum()), p.getHeaderImgUri()}
        );
        md.closeDB();
        return idNew;
    }

    private Project buildProjectFromCursor(Cursor cursor) {
        Project projectSelected = null;
        while (cursor.moveToNext() && projectSelected == null) {
            int colID = cursor.getColumnIndex(COL_ID);
            int colName = cursor.getColumnIndex(COL_NAME);
            int colDate = cursor.getColumnIndex(COL_CREATION_DATE);
            int colLap = cursor.getColumnIndex(COL_LAP);
            int colNeedle = cursor.getColumnIndex(COL_NEEDLE_NUM);
            int colHeaderImg = cursor.getColumnIndex(COL_HEADER_IMG_URI);

            projectSelected = new Project();
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
        }
        return projectSelected;
    }
}
