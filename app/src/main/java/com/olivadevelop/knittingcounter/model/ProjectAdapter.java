package com.olivadevelop.knittingcounter.model;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.tools.ImagePicasso;

import java.io.File;

public class ProjectAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private Context context;

    public ProjectAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView projectHomeImg = view.findViewById(R.id.projectHomeImg);
        TextView projectName = view.findViewById(R.id.projectName);
        TextView projectLastUpdate = view.findViewById(R.id.projectLastUpdate);
        TextView projectCounter = view.findViewById(R.id.projectCounter);
        TextView projectNeedle = view.findViewById(R.id.projectNeedle);
        if (cursor != null) {
            Project p = ProjectController.getInstance().buildProjectFromCursor(cursor);
            if (p != null) {
                String needle = this.context.getString(R.string.label_needle) + ": " + p.getNeedleNum();

                projectName.setText(p.getName());
                projectLastUpdate.setText(p.getCreationDate());
                projectCounter.setText(String.valueOf(p.getLap()));
                projectNeedle.setText(needle);

                String uriStr = p.getHeaderImgUri();
                if (uriStr != null) {
                    projectHomeImg.setVisibility(View.VISIBLE);
                    ImagePicasso.load(new File(uriStr), projectHomeImg);
                } else {
                    projectHomeImg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.inflater.inflate(R.layout.item_project, parent, false);
    }
}