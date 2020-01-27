package com.olivadevelop.knittingcounter.db.controllers;

import android.content.Context;
import android.database.Cursor;

import com.olivadevelop.knittingcounter.db.ManageDatabase;
import com.olivadevelop.knittingcounter.model.Gallery;
import com.olivadevelop.knittingcounter.tools.ToolsProject;

import java.io.File;

import static com.olivadevelop.knittingcounter.model.Gallery.COL_CREATION_DATE;
import static com.olivadevelop.knittingcounter.model.Gallery.COL_ID;
import static com.olivadevelop.knittingcounter.model.Gallery.COL_ID_PROJECT;
import static com.olivadevelop.knittingcounter.model.Gallery.COL_IMG_URI;
import static com.olivadevelop.knittingcounter.model.Gallery.COL_NAME;
import static com.olivadevelop.knittingcounter.model.Gallery.COL_OPTION_CREATE_IMG;

public class GalleryController {
    private final static GalleryController instance = new GalleryController();

    public static GalleryController getInstance() {
        return instance;
    }

    private static final String[] SELECT_ALL_FIELDS = {COL_ID, COL_ID_PROJECT, COL_NAME, COL_CREATION_DATE, COL_IMG_URI, COL_OPTION_CREATE_IMG};

    public Cursor findAll(Context c, long idProject) {
        if (idProject < 0) {
            return null;
        }
        ManageDatabase md = new ManageDatabase(c, true);
        return md.select(ManageDatabase.TABLE_GALLERY, SELECT_ALL_FIELDS, COL_ID + " DESC", COL_ID_PROJECT + " = ?", new String[]{String.valueOf(idProject)});
    }

    public Gallery findById(Context c, long projectId) {
        if (projectId < 1) {
            return null;
        }
        return find(c, COL_ID + " = ?", new String[]{String.valueOf(projectId)});
    }

    public Gallery findByProject(Context c, Integer projectId) {
        if (projectId != null) {
            return null;
        }
        return find(c, COL_ID_PROJECT + " = ?", new String[]{String.valueOf(projectId)});
    }

    public Gallery find(Context c, String whereClause, String[] whereArgs) {
        ManageDatabase md = new ManageDatabase(c, true);
        Cursor cursor = md.select(ManageDatabase.TABLE_GALLERY, SELECT_ALL_FIELDS, null, whereClause, whereArgs);
        Gallery projectSelected = null;
        if (cursor.moveToNext()) {
            projectSelected = buildGalleryFromCursor(cursor);
        }
        md.closeDB();
        return projectSelected;
    }

    public long create(Context c, Gallery g) {
        ManageDatabase md = new ManageDatabase(c, false);
        long idNew = md.insert(ManageDatabase.TABLE_GALLERY,
                new String[]{COL_ID_PROJECT, COL_NAME, COL_CREATION_DATE, COL_IMG_URI, COL_OPTION_CREATE_IMG},
                new String[]{String.valueOf(g.getId_project()), g.getName().trim(), g.getCreationDate(), g.getImgUri(), String.valueOf(g.getOptionCreateImage())}
        );
        md.closeDB();
        return idNew;
    }

    public boolean delete(Context c, Gallery g) {
        if (ToolsProject.TAKE_PICTURE == g.getOptionCreateImage()) {
            File img = new File(g.getImgUri());
            if (img.delete()) {
                System.out.println("Imagen eliminada");
            }
        }
        ManageDatabase md = new ManageDatabase(c, false);
        int affectedRows = md.delete(ManageDatabase.TABLE_GALLERY, COL_ID + " = ?", new String[]{String.valueOf(g.get_id())});
        md.closeDB();
        return affectedRows > 0;
    }

    public Gallery buildGalleryFromCursor(Cursor cursor) {
        int colID = cursor.getColumnIndex(COL_ID);
        int colIProject = cursor.getColumnIndex(COL_ID_PROJECT);
        int colName = cursor.getColumnIndex(COL_NAME);
        int colDate = cursor.getColumnIndex(COL_CREATION_DATE);
        int colImg = cursor.getColumnIndex(COL_IMG_URI);
        int colOptionCreateImg = cursor.getColumnIndex(COL_OPTION_CREATE_IMG);

        Gallery gallerySelected = new Gallery();
        if (colID > -1) {
            gallerySelected.set_id(cursor.getInt(colID));
        }
        if (colIProject > -1) {
            gallerySelected.setId_project(cursor.getInt(colIProject));
        }
        if (colName > -1) {
            gallerySelected.setName(cursor.getString(colName));
        }
        if (colDate > -1) {
            gallerySelected.setCreationDate(cursor.getString(colDate));
        }
        if (colImg > -1) {
            gallerySelected.setImgUri(cursor.getString(colImg));
        }
        if (colOptionCreateImg > -1) {
            gallerySelected.setOptionCreateImage(cursor.getInt(colOptionCreateImg));
        }
        return gallerySelected;
    }
}
