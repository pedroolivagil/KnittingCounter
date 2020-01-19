package com.olivadevelop.knittingcounter.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ManageDatabase;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private MainActivity mainActivity;
    private View root;

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
                }
            });
        }
        return root;
    }

    @Override
    public void onResume() {
        ManageDatabase md = new ManageDatabase(this.getContext(), true);
        /*md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"1", "Test Project 1", new Date().toString(), String.valueOf(0d)});
        md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"2", "Test Project 2", new Date().toString(), String.valueOf(0d)});
        md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"3", "Test Project 3", new Date().toString(), String.valueOf(0d)});
        md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"4", "Test Project 4", new Date().toString(), String.valueOf(0d)});
        md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"5", "Test Project 5", new Date().toString(), String.valueOf(0d)});
        md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"6", "Test Project 6", new Date().toString(), String.valueOf(0d)});
        md.insert(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, new String[]{"7", "Test Project 7", new Date().toString(), String.valueOf(0d)});*/
        Cursor items = md.select(ManageDatabase.TABLE_PROJECTS, new String[]{"_id", "name", "creation_date", "lap"}, "_id DESC", null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(),
                R.layout.item_project,
                items,
                new String[]{"name", "creation_date"},
                new int[]{R.id.projectName, R.id.projectLastUpdate});

        ListView listView = root.findViewById(R.id.projectList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        md.closeDB();
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Tools.newSnackBarWithIcon(view, parent.getContext(), R.string.app_name, android.R.drawable.stat_sys_warning);
        //Snackbar.make(view, "Project '" + id + "' selected", Snackbar.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        bundle.putLong("idProjectSelected", id);
        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_gallery, bundle);
    }
}