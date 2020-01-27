package com.olivadevelop.knittingcounter.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static com.olivadevelop.knittingcounter.tools.Tools.ID_PROJECT_SELECTED;

public class GalleryFragment extends Fragment {

    private MainActivity mainActivity;
    private View root;
    private Project projectSelected;


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
//                TextView textView = this.root.findViewById(R.id.text_gallery);
//                textView.setText(this.projectSelected.getName());
//                if (this.projectSelected.getHeaderImgUri() != null) {
//                    ImageView imgHeader = this.root.findViewById(R.id.view_project_img_header);
//                    imgHeader.setImageURI(Uri.parse(this.projectSelected.getHeaderImgUri()));
//                }
            }
        }
    }

    @Override
    public void onResume() {
        this.mainActivity.showFabButton();
        this.mainActivity.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add image to gallery
            }
        });
        this.mainActivity.hideImputMedia(this.root);
        super.onResume();
    }
}