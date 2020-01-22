package com.olivadevelop.knittingcounter.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.ProjectAdapter;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private MainActivity mainActivity;
    private View root;

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
            findProjects();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_home, container, false);
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
                    mainActivity.openDrawerMenu(v);
                }
            });
        }
        return root;
    }

    @Override
    public void onResume() {
        findProjects();
        this.mainActivity.showFabButton();
        this.mainActivity.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_slideshow);
            }
        });
        this.mainActivity.hideImputMedia(this.root);
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("idProjectSelected", id);
        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_gallery, bundle);
    }

    private void findProjects() {
        Cursor items = ProjectController.getInstance().findAll(this.mainActivity);
        if (items.getCount() == 0) {
            createDefault();
        }
        ProjectAdapter adapter = new ProjectAdapter(this.mainActivity, items);

        ListView listView = root.findViewById(R.id.projectList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void createDefault() {
        Project p = new Project();
        p.setName(getString(R.string.default_project_name));
        p.setNeedleNum(4.5f);
        p.setLap(34);
        ProjectController.getInstance().create(this.mainActivity, p);
    }
}