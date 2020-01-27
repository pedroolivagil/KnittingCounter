package com.olivadevelop.knittingcounter.model;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.GalleryController;
import com.olivadevelop.knittingcounter.tools.ImagePicasso;

import java.io.File;

public class GalleryAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private Context context;

    public GalleryAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView img = view.findViewById(R.id.item_img_gallery);
        if (cursor != null) {
            Gallery g = GalleryController.getInstance().buildGalleryFromCursor(cursor);
            if (g != null) {
                ImagePicasso.load(new File(g.getImgUri()), img);
            }
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.inflater.inflate(R.layout.item_gallery, parent, false);
    }
}