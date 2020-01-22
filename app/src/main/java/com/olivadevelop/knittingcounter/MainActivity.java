package com.olivadevelop.knittingcounter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private Snackbar customSnackbar;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.fab = findViewById(R.id.fab);

        this.drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
    public void onBackPressed() {
        if (this.customSnackbar != null) {
            this.customSnackbar.dismiss();
            this.customSnackbar = null;
            super.onBackPressed();
        }
        if (this.currentFragment != null && this.currentFragment.getClass().equals(HomeFragment.class)) {
            AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
            mensaje.setTitle(R.string.title_exit_app);
            mensaje.setCancelable(false);
            mensaje.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            mensaje.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mensaje.show();
        }
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

    public void hideImputMedia(View view) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    @SuppressLint("RtlHardcoded")
    public void openDrawerMenu(View v) {
        this.drawer.openDrawer(Gravity.LEFT);
    }

    public void hideFabButton() {
        this.fab.hide();
    }

    public void showFabButton() {
        this.fab.show();
    }

    public void setCustomSnackbar(Snackbar customSnackbar) {
        this.customSnackbar = customSnackbar;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }
}
