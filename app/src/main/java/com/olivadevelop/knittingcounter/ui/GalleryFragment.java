package com.olivadevelop.knittingcounter.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.GalleryController;
import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.model.Gallery;
import com.olivadevelop.knittingcounter.model.GalleryAdapter;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.Tools;
import com.olivadevelop.knittingcounter.tools.ToolsProject;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static com.olivadevelop.knittingcounter.tools.Tools.ID_PROJECT_SELECTED;

public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ToolsProject toolsProject;
    private Project projectSelected;
    private MainActivity mainActivity;
    private View root;
    private int requestCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_update_list) {
            updateGridGallery();
            this.mainActivity.customSnackBar(this.root, R.string.label_updating, R.drawable.ic_done_black_18dp, Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_gallery, container, false);
        this.mainActivity = (MainActivity) this.getActivity();
        if (this.mainActivity != null) {
            this.mainActivity.hideImputMedia(this.root);
            this.toolsProject = new ToolsProject(this, this.root);
        }
        if (this.mainActivity != null) {
            Toolbar toolbar = this.mainActivity.findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.onBackPressed();
                }
            });
            this.mainActivity.setCurrentFragment(this);
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.mainActivity != null) {
            NavController navController = Navigation.findNavController(view);
            // Set up the ActionBar to stay in sync with the NavController
            setupActionBarWithNavController(this.mainActivity, navController);
        }
        if (getArguments() != null) {
            long idProject = getArguments().getLong(ID_PROJECT_SELECTED);
            this.projectSelected = ProjectController.getInstance().findById(this.mainActivity, idProject);
            if (this.projectSelected != null) {
                updateGridGallery();
            }
        }
    }

    @Override
    public void onResume() {
        this.mainActivity.showFabButton();
        this.mainActivity.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(root.getContext());
                mBuilder.setTitle(getString(R.string.dialog_choose_action_title));
                final String[] listItems = new String[]{getString(R.string.label_img_camera), getString(R.string.label_img_gallery)};
                mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addImageToGallery(i + 1);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        this.mainActivity.hideImputMedia(this.root);
        super.onResume();
    }

    private void addImageToGallery(int option) {
        try {
            System.out.println("OptionSelected: " + option);
            if (option == ToolsProject.TAKE_PICTURE) {
                toolsProject.takePhotoFromCamera(projectSelected.getName());
            } else if (option == ToolsProject.SELECT_PICTURE) {
                toolsProject.takePhotoFromGallery();
            } else {
                mainActivity.customSnackBar(root, R.string.error_image_new_project, R.drawable.ic_warning_black_18dp, Snackbar.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            mainActivity.customSnackBar(root, R.string.error_image_new_project, R.drawable.ic_warning_black_18dp, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = null;
                if (requestCode == ToolsProject.TAKE_PICTURE) {
                    bitmap = this.toolsProject.resultFromTakePhotoFromCamera(data);
                    this.requestCode = ToolsProject.TAKE_PICTURE;
                } else if (requestCode == ToolsProject.SELECT_PICTURE) {
                    bitmap = this.toolsProject.resultFromTakePhotoFromGallery(data);
                    this.requestCode = ToolsProject.SELECT_PICTURE;
                }
                if (bitmap != null) {
                    createGallery();
                }
            } catch (Exception e) {
                this.mainActivity.customSnackBar(this.root, R.string.error_image_new_project, R.drawable.ic_warning_black_18dp, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void createGallery() {
        Gallery g = new Gallery();
        g.setId_project(this.projectSelected.get_id());
        g.setImgUri(this.toolsProject.getCurrentPhotoPath());
        g.setName(this.projectSelected.getName());
        g.setOptionCreateImage(this.requestCode);
        long idNew = GalleryController.getInstance().create(this.mainActivity, g);

        if (idNew > 0) {
            updateGridGallery();
            this.mainActivity.customSnackBar(this.root, R.string.label_new_project_ok, R.drawable.ic_done_black_18dp, Snackbar.LENGTH_LONG).show();
            Tools.timerExecute(this.mainActivity, 1500f, new Runnable() {
                @Override
                public void run() {
                    updateGridGallery();
                }
            });
        } else {
            this.mainActivity.customSnackBar(this.root, R.string.error_new_project, R.drawable.ic_warning_black_18dp, Snackbar.LENGTH_LONG).show();
        }
    }

    private void updateGridGallery() {
        Cursor images = GalleryController.getInstance().findAll(this.mainActivity, this.projectSelected.get_id());
        if (images != null) {
            GalleryAdapter ga = new GalleryAdapter(this.mainActivity, images);

            GridView gv = this.root.findViewById(R.id.gallery_grid_view);
            gv.setAdapter(ga);
            gv.setOnItemClickListener(this);
            gv.setOnItemLongClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Gallery image = GalleryController.getInstance().findById(this.mainActivity, id);
        AlertDialog.Builder mensaje = new AlertDialog.Builder(this.mainActivity);
        mensaje.setTitle(R.string.dialog_choose_action_title);
        mensaje.setCancelable(false);
        mensaje.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GalleryController.getInstance().delete(mainActivity, image);
                updateGridGallery();
            }
        });
        mensaje.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mensaje.show();
        return false;
    }
}