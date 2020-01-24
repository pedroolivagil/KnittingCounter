package com.olivadevelop.knittingcounter.tools;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.ui.HomeFragment;

import static com.olivadevelop.knittingcounter.tools.Tools.ID_PROJECT_SELECTED;

public class ProjectHomeDialog extends Dialog implements View.OnClickListener {

    private HomeFragment fragment;
    private final View view;
    private Project project;

    private Button btnEdit;
    private Button btnDelete;

    public ProjectHomeDialog(@NonNull Fragment fragment, @NonNull Project project) {
        super(fragment.getActivity(), true, null);
        this.fragment = (HomeFragment) fragment;
        this.project = project;
        this.view = this.fragment.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.project_home_dialog);

        this.btnEdit = findViewById(R.id.project_home_dialog_btn_edit);
        this.btnDelete = findViewById(R.id.project_home_dialog_btn_delete);

        this.btnEdit.setOnClickListener(this);
        this.btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == this.btnDelete) {
            ProjectController.getInstance().delete(getContext(), this.project);
            this.fragment.findProjects();
        } else if (v == btnEdit) {
            Bundle bundle = new Bundle();
            bundle.putLong(ID_PROJECT_SELECTED, this.project.get_id());
            Navigation.findNavController(this.view).navigate(R.id.action_nav_home_to_nav_edit_project, bundle);
        }
        dismiss();
    }
}
