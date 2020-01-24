package com.olivadevelop.knittingcounter.tools;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public abstract class Tools {

    public static final String ID_PROJECT_SELECTED = "idProjectSelected";

    public static Snackbar newSnackBarWithIcon(View v, Context cnxt, int string, int icon) {
        Snackbar snackbar = Snackbar.make(v, string, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        textView.setCompoundDrawablePadding(cnxt.getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding));
        return snackbar;
    }

    public static String generateID() {
        return UUID.randomUUID().toString();
    }

    public static String formatDate(Date date) {
        return formatDate(date, "dd/MM/yyyy");
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }

    public static String getRealPathFromURI(Context mContext, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static boolean isNotEmpty(Editable text) {
        return text != null && !text.toString().trim().isEmpty();
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static void timerExecute(final Activity a, final float timeMillis, final Runnable action) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while ((waited < timeMillis)) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    a.runOnUiThread(action);
                }
            }
        };
        thread.start();
    }

    public static Thread executeInThread(final Activity a, final Runnable action) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    a.runOnUiThread(action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setName(Tools.generateID());
        thread.start();
        return thread;
    }

    public static void actionHoldPressView(final View view, final Runnable task) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) {
                        return true;
                    }
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 1);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mHandler == null) {
                        return true;
                    }
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    task.run();
                    mHandler.postDelayed(this, 250);
                }
            };
        });
    }
}