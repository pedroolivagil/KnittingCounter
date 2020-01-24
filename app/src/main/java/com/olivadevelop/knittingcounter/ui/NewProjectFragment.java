package com.olivadevelop.knittingcounter.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.Tools;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class NewProjectFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private View root;
    private EditText projectName;
    private EditText projectNeedleNum;
    private LinearLayout lytBtnCamera;
    private LinearLayout lytBtnGallery;
    private ImageView imageThumb;

    private int requestCode = 0;
    //    private String currentPhotoPath;
    private ToolsProject tools;

    private Snackbar customSnackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.new_project, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save_project) {
            createProject();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_new_project, container, false);
        this.mainActivity = (MainActivity) this.getActivity();
        if (this.mainActivity != null) {
            Toolbar toolbar = this.mainActivity.findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.setCustomSnackbar(customSnackbar);
                    mainActivity.onBackPressed();
                }
            });
            this.mainActivity.setCurrentFragment(this);
            this.tools = new ToolsProject(this, this.root);
        }

        this.projectName = this.root.findViewById(R.id.project_name);
        this.projectNeedleNum = this.root.findViewById(R.id.project_needle);

        this.lytBtnCamera = this.root.findViewById(R.id.btnCamera);
        this.lytBtnGallery = this.root.findViewById(R.id.btnGalery);
        this.imageThumb = this.root.findViewById(R.id.image_thumb);

        this.lytBtnCamera.setOnClickListener(this);
        this.lytBtnGallery.setOnClickListener(this);

        resetForm();

        return this.root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the NavController for your NavHostFragment
        if (this.mainActivity != null) {
            NavController navController = Navigation.findNavController(view);
            // Set up the ActionBar to stay in sync with the NavController
            setupActionBarWithNavController(this.mainActivity, navController);
        }
    }

    @Override
    public void onResume() {
        this.mainActivity.hideFabButton();
        this.mainActivity.hideImputMedia(this.root);
        this.mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (this.customSnackbar != null) {
            this.customSnackbar.dismiss();
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == lytBtnCamera) {
                this.tools.takePhotoFromCamera(this.projectName.getText().toString());
            } else if (v == lytBtnGallery) {
                this.tools.takePhotoFromGallery();
            }
        } catch (Exception e) {
            this.customSnackbar = this.mainActivity.customSnackBar(this.root, R.string.error_image_new_project, R.drawable.ic_warning_black_18dp);
            this.customSnackbar.show();
            this.mainActivity.setCustomSnackbar(this.customSnackbar);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.requestCode = requestCode;
            try {
                Bitmap bitmap = null;
                if (requestCode == ProjectController.TAKE_PICTURE) {
                    bitmap = this.tools.resultFromTakePhotoFromCamera(data);
                } else if (requestCode == ProjectController.SELECT_PICTURE) {
                    bitmap = this.tools.resultFromTakePhotoFromGallery(data);
                }
                if (bitmap != null) {
                    this.imageThumb.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                this.customSnackbar = this.mainActivity.customSnackBar(this.root, R.string.error_image_new_project, R.drawable.ic_warning_black_18dp);
                this.customSnackbar.show();
                this.mainActivity.setCustomSnackbar(this.customSnackbar);
            }
        }
    }

    private void resetForm() {
        this.tools.setCurrentPhotoPath(null);
        this.projectName.setText("");
        this.projectNeedleNum.setText("0");
        this.imageThumb.setImageDrawable(getResources().getDrawable(R.drawable.ic_crop_free_black_24dp));
        this.requestCode = 0;
    }

    private void createProject() {
        this.mainActivity.hideImputMedia(this.root);
        if (!Tools.isNotEmpty(this.projectName.getText()) && !Tools.isNotEmpty(this.projectNeedleNum.getText())) {
            this.customSnackbar = this.mainActivity.customSnackBar(this.root, R.string.error_new_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createProject();
                }
            });
            this.customSnackbar.show();
            this.mainActivity.setCustomSnackbar(this.customSnackbar);
            return;
        }
        Project pExists = ProjectController.getInstance().findByName(this.mainActivity, this.projectName.getText().toString());
        if (pExists != null) {
            this.customSnackbar = this.mainActivity.customSnackBar(this.root, R.string.error_new_project_already_exists, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_clean, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetForm();
                }
            });
            this.customSnackbar.show();
            this.mainActivity.setCustomSnackbar(this.customSnackbar);
            return;
        }

        Project p = new Project();
        p.setName(this.projectName.getText().toString());
        p.setHeaderImgUri(this.tools.getCurrentPhotoPath());
        p.setNeedleNum(Float.valueOf(this.projectNeedleNum.getText().toString()));
        p.setOptionHeaderImage(this.requestCode);
        long idNew = ProjectController.getInstance().create(this.mainActivity, p);

        if (idNew > 0) {
            resetForm();
            this.mainActivity.customSnackBar(this.root, R.string.label_new_project_ok, R.drawable.ic_done_black_18dp, Snackbar.LENGTH_LONG).show();
            Tools.timerExecute(this.mainActivity, 2500f, new Runnable() {
                @Override
                public void run() {
                    Navigation.findNavController(root).navigate(R.id.action_nav_new_project_to_nav_home);
                }
            });
        } else {
            this.customSnackbar = this.mainActivity.customSnackBar(this.root, R.string.error_new_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createProject();
                }
            });
            this.customSnackbar.show();
            this.mainActivity.setCustomSnackbar(this.customSnackbar);
        }
    }
}