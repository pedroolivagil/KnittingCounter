package com.olivadevelop.knittingcounter.tools;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.controllers.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;

public class ProjectHomeDialog extends Dialog implements View.OnClickListener {

    private final View view;
    private Project project;

    private Button btnEdit;
    private Button btnDelete;

    public ProjectHomeDialog(Activity context, View view, Project project) {
        super(context, true, null);
        this.project = project;
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.project_home_dialog);


        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.x = -200; // left margin
        layoutParams.horizontalMargin = -200;
        getWindow().setAttributes(layoutParams);

        this.btnEdit = findViewById(R.id.project_home_dialog_btn_edit);
        this.btnDelete = findViewById(R.id.project_home_dialog_btn_delete);

        this.btnEdit.setOnClickListener(this);
        this.btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == this.btnDelete) {
            ProjectController.getInstance().delete(getContext(), this.project);
        } else if (v == btnEdit) {
            Bundle bundle = new Bundle();
            bundle.putLong("idProjectSelected", this.project.get_id());
            Navigation.findNavController(this.view).navigate(R.id.action_nav_home_to_nav_edit_project, bundle);
        }
        dismiss();
    }
}
