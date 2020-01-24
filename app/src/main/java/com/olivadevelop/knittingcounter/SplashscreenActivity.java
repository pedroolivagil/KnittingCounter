package com.olivadevelop.knittingcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.Tools;

public class SplashscreenActivity extends AppCompatActivity {

    protected float segundos = 2.5f;
    protected float splashTime = segundos * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Tools.timerExecute(this, splashTime, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashscreenActivity.this, MainActivity.class));
                finish();
            }
        });
        Tools.executeInThread(this, new Runnable() {
            @Override
            public void run() {
                createDefault();
            }
        });
    }

    private void createDefault() {
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            Project p = new Project();
            p.setName(getString(R.string.default_project_name));
            p.setNeedleNum(4.5f);
            p.setLap(34);
            ProjectController.getInstance().create(this, p);

            // Save first run
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.apply();
        }
    }
}
