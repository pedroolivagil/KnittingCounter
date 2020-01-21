package com.olivadevelop.knittingcounter.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ManageDatabase;
import com.olivadevelop.knittingcounter.tools.Tools;

import java.util.Date;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private HomeViewModel homeViewModel;
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
        if (item.getItemId() == R.id.action_save_project) {
            findProjects();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        this.root = inflater.inflate(R.layout.fragment_home, container, false);
        this.mainActivity = (MainActivity) this.getActivity();

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
        this.mainActivity.hideImputMedia();
        this.mainActivity.showFabButton();
        this.mainActivity.getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_slideshow);
            }
        });
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("idProjectSelected", id);
        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_gallery, bundle);
    }

    private void findProjects() {
        ManageDatabase md = new ManageDatabase(this.getContext(), true);
        Cursor items = md.select(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap", "needle_num", "header_img_uri"}, "_id DESC");
        if (items.getCount() == 0) {
            createDefault(md);
        }
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(),
                R.layout.item_project,
                items,
                new String[]{"name", "creation_date"},
                new int[]{R.id.projectName, R.id.projectLastUpdate});

        ListView listView = root.findViewById(R.id.projectList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        md.closeDB();
    }

    private void createDefault(ManageDatabase md) {
        md.insert(ManageDatabase.TABLE_PROJECTS,
                new String[]{"_id", "name", "creation_date", "lap", "needle_num"},
                new String[]{String.valueOf(1), getString(R.string.default_project_name), Tools.formatDate(new Date()), String.valueOf(0d), String.valueOf(3d)}
        );
    }
}