package com.olivadevelop.knittingcounter.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.model.Project;

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
            int colName = cursor.getColumnIndex(Project.COL_NAME);
            int colDate = cursor.getColumnIndex(Project.COL_CREATION_DATE);
            int colLap = cursor.getColumnIndex(Project.COL_LAP);
            int colNeedle = cursor.getColumnIndex(Project.COL_NEEDLE_NUM);
            int colHeaderImg = cursor.getColumnIndex(Project.COL_HEADER_IMG_URI);

            String needle = this.context.getString(R.string.label_needle) + " " + cursor.getString(colNeedle);

            String tempName = cursor.getString(colName) + " (" + cursor.getInt(cursor.getColumnIndex(Project.COL_ID)) + ")";
            projectName.setText(tempName);
//            projectName.setText(cursor.getString(colName));
            projectLastUpdate.setText(cursor.getString(colDate));
            projectCounter.setText(cursor.getString(colLap));
            projectNeedle.setText(needle);

            String uriStr = cursor.getString(colHeaderImg);
            if (uriStr != null) {
                projectHomeImg.setImageURI(Uri.parse(uriStr));
                projectHomeImg.setVisibility(View.VISIBLE);
            } else {
                projectHomeImg.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.inflater.inflate(R.layout.item_project, parent, false);
    }
}