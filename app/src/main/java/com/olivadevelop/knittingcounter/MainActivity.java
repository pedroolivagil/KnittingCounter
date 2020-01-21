package com.olivadevelop.knittingcounter;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton fab;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.fab = findViewById(R.id.fab);
        this.fab.setOnClickListener(this);

        this.drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //, R.id.nav_slideshow, R.id.nav_tools, R.id.nav_share, R.id.nav_send
        this.mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(this.drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
        /* if (view == fab) {
         *//* Intent intent = new Intent(this, NewProject.class);
            startActivity(intent);*//*
            Snackbar.make(view, "Crear proyecto", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }*/
    }

    public Snackbar customSnackBar(View v, @StringRes int text, @DrawableRes int icon) {
        return customSnackBar(v, text, icon, Snackbar.LENGTH_INDEFINITE);
    }

    public Snackbar customSnackBar(View v, @StringRes int text, @DrawableRes int icon, int duration) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(" ");
        builder.setSpan(new ImageSpan(v.getContext(), icon), builder.length() - 1, builder.length(), 0);
        builder.append(" ").append(getString(text));
        final Snackbar snackbar = Snackbar.make(v, builder, duration);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        if (duration == Snackbar.LENGTH_INDEFINITE) {
            snackbar.setAction(R.string.label_close_snackbar, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
        }
        return snackbar;
    }

    public void hideImputMedia() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public void openDrawerMenu(View v) {
        this.drawer.openDrawer(Gravity.LEFT);
    }

    public void hideFabButton() {
        this.fab.hide();
    }

    public void showFabButton() {
        this.fab.show();
    }
}
