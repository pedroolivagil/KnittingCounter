package com.olivadevelop.knittingcounter.ui.gallery;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ManageDatabase;
import com.olivadevelop.knittingcounter.model.Project;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class GalleryFragment extends Fragment implements View.OnClickListener {

    private GalleryViewModel galleryViewModel;
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
        inflater.inflate(R.menu.empty, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        this.root = inflater.inflate(R.layout.fragment_gallery, container, false);
        this.mainActivity = (MainActivity) this.getActivity();

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
            ManageDatabase md = new ManageDatabase(this.getContext(), true);
            Cursor items = md.select(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, null, "_id = ?", new String[]{String.valueOf(idProject)});
            while (items.moveToNext() && projectSelected == null) {
                this.projectSelected = new Project();
                this.projectSelected.set_id(items.getInt(0));
                this.projectSelected.setName(items.getString(1));
                this.projectSelected.setCreation_date(items.getString(2));
                this.projectSelected.setLap(items.getInt(3));
            }
            if (projectSelected != null) {
                TextView textView = root.findViewById(R.id.text_gallery);
                textView.setText(this.projectSelected.getName());
                updateTextCounter();
            }
            md.closeDB();
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
        ContentValues cv = new ContentValues();
        cv.put("lap", projectSelected.getLap());
        ManageDatabase md = new ManageDatabase(this.getContext(), true);
        md.update(ManageDatabase.TABLE_PROJECTS, cv, "_id = ?", new String[]{String.valueOf(projectSelected.get_id())});
        md.closeDB();

        updateTextCounter();
    }

    private void updateTextCounter() {
        this.textCounter.setText("" + this.projectSelected.getLap());
    }
}