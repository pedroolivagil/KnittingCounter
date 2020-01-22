package com.olivadevelop.knittingcounter.ui.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;

import java.io.File;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class GalleryFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private View root;

    private Project projectSelected = null;
    private TextView textCounter;
    private ImageButton btnReset;
    private ImageButton btnAmount;
    private ImageButton btnSubtract;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.project, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_project) {
            deleteProject();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_gallery, container, false);
        this.mainActivity = (MainActivity) this.getActivity();
        if (this.mainActivity != null) {
            this.mainActivity.hideImputMedia(this.root);
        }

        if (this.mainActivity != null) {
            Toolbar toolbar = this.mainActivity.findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.onBackPressed();
                }
            });
        }

        this.textCounter = this.root.findViewById(R.id.textCounter);

        this.btnReset = this.root.findViewById(R.id.btnReset);
        this.btnSubtract = this.root.findViewById(R.id.btnSubtract);
        this.btnAmount = this.root.findViewById(R.id.buttonAdd);
        this.btnReset.setOnClickListener(this);
        this.btnAmount.setOnClickListener(this);
        this.btnSubtract.setOnClickListener(this);
        return this.root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            // Get the NavController for your NavHostFragment
            if (this.mainActivity != null) {
                NavController navController = Navigation.findNavController(view);
                // Set up the ActionBar to stay in sync with the NavController
                setupActionBarWithNavController(this.mainActivity, navController);
            }
            long idProject = getArguments().getLong("idProjectSelected");
            this.projectSelected = ProjectController.getInstance().find(this.mainActivity, "_id = ?", new String[]{String.valueOf(idProject)});
            if (this.projectSelected != null) {
                TextView textView = this.root.findViewById(R.id.text_gallery);
                textView.setText(this.projectSelected.getName());
                updateTextCounter();
            }
            //md.closeDB();
        }
    }

    @Override
    public void onResume() {
        this.mainActivity.hideFabButton();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            this.projectSelected.setLap(0);
        } else if (v == btnAmount) {
            this.projectSelected.addLap();
        } else if (v == btnSubtract) {
            this.projectSelected.removeLap();
        }
        ProjectController.getInstance().update(this.mainActivity, this.projectSelected);
        updateTextCounter();
    }

    private void updateTextCounter() {
        this.textCounter.setText(String.valueOf(this.projectSelected.getLap()));
    }

    private void _deleteProject() {
        try {
            // Remove header img
            if (this.projectSelected.getHeaderImgUri() != null) {
                File file = new File(this.projectSelected.getHeaderImgUri());
                if (file.exists() && file.delete()) {
                    deleteProjectData();
                }
            } else {
                deleteProjectData();
            }
        } catch (Exception e) {
            this.mainActivity.customSnackBar(this.root, R.string.error_delete_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _deleteProject();
                }
            }).show();
        }
    }

    private void deleteProjectData() {
        ProjectController.getInstance().delete(this.mainActivity, this.projectSelected);
        Navigation.findNavController(this.root).navigate(R.id.action_nav_gallery_to_nav_home);
    }

    private void deleteProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.root.getContext());
        builder.setTitle(R.string.title_dialog_delete);
        builder.setMessage(R.string.label_dialog_delete);
        // Add the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                _deleteProject();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}