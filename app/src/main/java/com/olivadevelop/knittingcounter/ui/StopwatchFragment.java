package com.olivadevelop.knittingcounter.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.olivadevelop.knittingcounter.model.TimerHistoric;
import com.olivadevelop.knittingcounter.tools.ImagePicasso;
import com.olivadevelop.knittingcounter.tools.Tools;
import com.olivadevelop.knittingcounter.ui.tools.SwitchImageButton;

import java.io.File;
import java.util.Date;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static com.olivadevelop.knittingcounter.tools.Tools.ID_PROJECT_SELECTED;
import static com.olivadevelop.knittingcounter.tools.Tools.actionHoldPressView;
import static com.olivadevelop.knittingcounter.tools.ToolsProject.SELECT_PICTURE;

public class StopwatchFragment extends Fragment {

    private MainActivity mainActivity;
    private View root;

    private Project projectSelected = null;
    private TextView textCounter;
    private ImageButton btnResetCounter;
    private SwitchImageButton btnPlayPause;
    private ImageButton btnStop;
    private ImageButton btnAddToList;
    private ImageButton btnRemoveFromList;
    private ImageButton btnDeleteList;
    private Button btnGoToCounter;

    private String nameHistoric = "";

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
        Bundle bundle = new Bundle();
        bundle.putLong(ID_PROJECT_SELECTED, this.projectSelected.get_id());
        boolean retorno;
        switch (item.getItemId()) {
            case R.id.action_delete_project:
                deleteProject();
                retorno = true;
                break;
            case R.id.action_edit_project:
                Navigation.findNavController(this.root).navigate(R.id.action_nav_project_to_nav_edit_project, bundle);
                retorno = true;
                break;
            case R.id.action_gallery_project:
                Navigation.findNavController(this.root).navigate(R.id.action_nav_project_to_nav_gallery, bundle);
                retorno = true;
                break;
            default:
                retorno = super.onOptionsItemSelected(item);
        }
        return retorno;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        this.mainActivity = (MainActivity) this.getActivity();
        if (this.mainActivity != null) {
            this.mainActivity.hideImputMedia(this.root);
            this.mainActivity.setCurrentFragment(this);
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

        this.btnResetCounter = this.root.findViewById(R.id.btnResetCounter);
        this.btnStop = this.root.findViewById(R.id.btnStop);
        this.btnPlayPause = new SwitchImageButton((ImageButton) this.root.findViewById(R.id.btnPlayPause));
        this.btnAddToList = this.root.findViewById(R.id.btnAddToList);
        this.btnRemoveFromList = this.root.findViewById(R.id.btnRemoveFromList);
        this.btnDeleteList = this.root.findViewById(R.id.btnDeleteList);

        this.btnGoToCounter = this.root.findViewById(R.id.btnCounterScreen);
        this.btnPlayPause.setActive(false);

        actionButtonView();

        return this.root;
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
                TextView textView = this.root.findViewById(R.id.text_gallery);
                textView.setText(this.projectSelected.getName());
                if (this.projectSelected.getHeaderImgUri() != null) {
                    ImageView imgHeader = this.root.findViewById(R.id.view_project_img_header);
                    ImagePicasso.load(new File(this.projectSelected.getHeaderImgUri()), imgHeader);
                }
                updateTextCounter();
            }
        }
    }

    @Override
    public void onResume() {
        this.mainActivity.hideFabButton();
        super.onResume();
    }

    private void actionButtonView() {
        this.btnGoToCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(ID_PROJECT_SELECTED, projectSelected.get_id());
                Navigation.findNavController(root).navigate(R.id.action_stopwatchFragment_to_nav_project, bundle);
            }
        });
        this.btnResetCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.customSnackBar(root, R.string.label_hold_btn_reset, R.drawable.ic_info_black_18dp, Snackbar.LENGTH_LONG).show();
            }
        });
        actionHoldPressView(this.btnResetCounter, new Runnable() {
            @Override
            public void run() {
                projectSelected.setTime(0L);
                boolean result = ProjectController.getInstance().update(mainActivity, projectSelected);
                if (result) {
                    updateTextCounter();
                }
            }
        });
        this.btnPlayPause.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayPauseSetActiveButon(v);
            }
        });
        // pausa el reloj y añade un registro del tiempo actual al historico. Al iniciar, el reloj empezará de 0.
        this.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectSelected.getHistoric().add(new TimerHistoric(nameHistoric, projectSelected.getTime()));
                projectSelected.setTime(0L);
                nameHistoric = "";
                boolean result = ProjectController.getInstance().update(mainActivity, projectSelected);
                if (result) {
                    updateTextCounter();
                }
            }
        });
        this.btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectSelected.getHistoric().add(new TimerHistoric(nameHistoric, projectSelected.getTime()));
                nameHistoric = "";
                boolean result = ProjectController.getInstance().update(mainActivity, projectSelected);
                if (result) {
                    updateTextCounter();
                }
            }
        });
    }

    private void updatePlayPauseSetActiveButon(View v) {
        btnPlayPause.setActive(!btnPlayPause.isActive());
        if (!btnPlayPause.isActive()) {
            btnPlayPause.getImageButton().setImageDrawable(v.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
        } else {
            btnPlayPause.getImageButton().setImageDrawable(v.getResources().getDrawable(R.drawable.ic_pause_black_24dp));
        }
    }

    private void updateTextCounter() {
        this.textCounter.setText(Tools.formatDate(new Date(this.projectSelected.getTime()), "HH:mm:ss"));
    }

    private void _deleteProject() {
        try {
            // Remove header img
            if (this.projectSelected.getHeaderImgUri() != null && this.projectSelected.getOptionHeaderImage() == SELECT_PICTURE) {
                File file = new File(this.projectSelected.getHeaderImgUri());
                if (file.exists()) {
                    file.delete();
                }
            }
            ProjectController.getInstance().delete(this.mainActivity, this.projectSelected);
            Navigation.findNavController(this.root).navigate(R.id.action_nav_project_to_nav_home);
        } catch (Exception e) {
            this.mainActivity.customSnackBar(this.root, R.string.error_delete_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _deleteProject();
                }
            }).show();
        }
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