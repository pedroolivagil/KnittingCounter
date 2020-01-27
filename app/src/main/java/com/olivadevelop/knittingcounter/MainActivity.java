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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
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
import com.olivadevelop.knittingcounter.tools.ADSAdmob;
import com.olivadevelop.knittingcounter.tools.Tools;
import com.olivadevelop.knittingcounter.ui.EditProjectFragment;
import com.olivadevelop.knittingcounter.ui.HomeFragment;
import com.olivadevelop.knittingcounter.ui.NewProjectFragment;
import com.olivadevelop.knittingcounter.ui.ViewProjectFragment;

public class MainActivity extends AppCompatActivity {

    private ADSAdmob ads;

    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private Snackbar customSnackbar;
    private NavController navController;
    private Fragment currentFragment;
    private Fragment beforeFragment;

    private int resultCodeFragment = Tools.RESULT_CODE_FRAGMENT_KO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.fab = findViewById(R.id.fab);

        this.drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        this.mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_new_project, R.id.nav_project)
                .setDrawerLayout(this.drawer).build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navigationDrawer(navigationView, navController);

        this.ads = new ADSAdmob(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (this.currentFragment != null && this.currentFragment.getClass().equals(HomeFragment.class)) {
            exitApp();
        } else if (this.currentFragment != null && this.currentFragment.getClass().equals(NewProjectFragment.class)) {
            newProjectDialog();
        } else if (this.currentFragment != null && this.currentFragment.getClass().equals(EditProjectFragment.class)) {
            editProjectDialog();
        } else {
            if (this.customSnackbar != null) {
                this.customSnackbar.dismiss();
                this.customSnackbar = null;
            }
            super.onBackPressed();
        }
        this.resultCodeFragment = Tools.RESULT_CODE_FRAGMENT_KO;
    }

    public void onBackPressed(int resultCode) {
        this.resultCodeFragment = resultCode;
        onBackPressed();
    }

    private void navigationDrawer(final NavigationView navigationView, final NavController navController) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navController.navigate(R.id.nav_home);
                        drawer.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.nav_new_project:
                        navController.navigate(R.id.nav_new_project);
                        drawer.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.exit_app:
                        exitApp();
                        drawer.closeDrawer(Gravity.LEFT);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
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

    public void exitApp() {
        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
        mensaje.setTitle(R.string.title_exit_app);
        mensaje.setCancelable(false);
        mensaje.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ads.showInterstitialAd();
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

    private void newProjectDialog() {
        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
        mensaje.setTitle(R.string.title_cancel_new_project);
        mensaje.setMessage(R.string.label_cancel_new_project);
        mensaje.setCancelable(false);
        mensaje.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                navController.navigate(R.id.action_nav_new_project_to_nav_home);
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

    private void editProjectDialog() {
        if (this.resultCodeFragment != Tools.RESULT_CODE_FRAGMENT_OK) {
            AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
            mensaje.setTitle(R.string.title_cancel_new_project);
            mensaje.setMessage(R.string.label_cancel_new_project);
            mensaje.setCancelable(false);
            mensaje.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    editProjectNavigate();
                }
            });
            mensaje.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mensaje.show();
        } else {
//            editProjectNavigate();
            super.onBackPressed();
        }
    }

    private void editProjectNavigate() {
        if (beforeFragment != null) {
            if (beforeFragment.getClass().equals(ViewProjectFragment.class)) {
//                navController.navigate(R.id.action_nav_edit_project_to_nav_project);
                super.onBackPressed();
            } else {
                navController.navigate(R.id.action_nav_edit_project_to_nav_home);
            }
        } else {
            navController.navigate(R.id.action_nav_edit_project_to_nav_home);
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
        this.beforeFragment = this.currentFragment;
        this.currentFragment = currentFragment;
    }

    public ADSAdmob getAds() {
        return ads;
    }
}
