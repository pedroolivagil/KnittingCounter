package com.olivadevelop.knittingcounter.tools;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.olivadevelop.knittingcounter.R;

public class ProjectAdapter extends SimpleCursorAdapter {

    private Context b;
    private LayoutInflater inflater;

    public ProjectAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv = (TextView) view.findViewById(R.id.);
        TextView tv = (TextView) view.findViewById(R.id.);
        TextView tv = (TextView) view.findViewById(R.id.);
        TextView tv = (TextView) view.findViewById(R.id.);

        // TODO corregir
        tv1.setText(cursor.getString(2));
        tv2.setText(cursor.getString(3));

        final int pos = cursor.getPosition();

        final CheckBox repeatChkBx = (CheckBox)view.findViewById(R.id.favorite_check);

        String me = cursor.getString(cursor.getColumnIndex("like"));

        if (me.equals("yes")) {
            repeatChkBx.setChecked(true);
        } else {
            repeatChkBx.setChecked(false);
        }

        repeatChkBx.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                MyDatabase MyDatabase = new MyDatabase(b);
                SQLiteDatabase mydb = MyDatabase.getWritableDatabase();
                cursor.moveToPosition(pos);

                if (repeatChkBx.isChecked()) {
                    mydb.execSQL("update list set like = 'yes' where id = " + cursor.getString(1));

                }else{
                    mydb.execSQL("update list set like = 'no' where id = " + cursor.getString(1));

                }
            }
        });
        super.bindView(view, context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }
}