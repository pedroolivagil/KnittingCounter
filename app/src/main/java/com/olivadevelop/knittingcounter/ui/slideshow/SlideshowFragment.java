package com.olivadevelop.knittingcounter.ui.slideshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.BuildConfig;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ManageDatabase;
import com.olivadevelop.knittingcounter.tools.PermissionsChecker;
import com.olivadevelop.knittingcounter.tools.Tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private SlideshowViewModel slideshowViewModel;
    private MainActivity mainActivity;
    private View root;
    private Button btnCreate;
    private EditText projectName;
    private EditText projectNeedleNum;
    private LinearLayout lytBtnCamera;
    private LinearLayout lytBtnGallery;
    private ImageView image_thumb;
    private String filename;
    private PermissionsChecker permissionsChecker;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        this.root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        this.mainActivity = (MainActivity) this.getActivity();
        this.permissionsChecker = new PermissionsChecker(this.mainActivity);

        if (this.mainActivity != null) {
            Toolbar toolbar = this.mainActivity.findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.onBackPressed();
                }
            });
        }
        this.btnCreate = this.root.findViewById(R.id.btnCreate);
        this.btnCreate.setOnClickListener(this);
        this.projectName = this.root.findViewById(R.id.project_name);
        this.projectNeedleNum = this.root.findViewById(R.id.project_needle);

        this.lytBtnCamera = this.root.findViewById(R.id.btnCamera);
        this.lytBtnGallery = this.root.findViewById(R.id.btnGalery);
        this.image_thumb = this.root.findViewById(R.id.image_thumb);

        File file = new File(Tools.getExternalStorage(this.mainActivity));
        file.mkdirs();

        this.lytBtnCamera.setOnClickListener(this);
        this.lytBtnGallery.setOnClickListener(this);

        return this.root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the NavController for your NavHostFragment
        if (this.mainActivity != null) {
            NavController navController = Navigation.findNavController(view);
            // Set up the ActionBar to stay in sync with the NavController
            setupActionBarWithNavController(this.mainActivity, navController);
        }
    }

    @Override
    public void onResume() {
        this.mainActivity.hideFabButton();
        this.mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnCreate) {
            ManageDatabase md = new ManageDatabase(this.getContext(), false);
            int id = md.count(ManageDatabase.TABLE_PROJECTS);
            md.insert(ManageDatabase.TABLE_PROJECTS,
                    new String[]{"_id", "name", "creation_date", "lap", "needle_num"},
                    new String[]{String.valueOf(id), this.projectName.getText().toString(), Tools.formatDate(new Date()), String.valueOf(0d), this.projectNeedleNum.getText().toString()}
            );
            md.closeDB();
        } else {
            int code = 0;
            Intent intent = null;
            if (v == lytBtnCamera) {
                if (this.permissionsChecker.checkStoragePermission() && this.permissionsChecker.checkCameraPermission()) {
                    filename = Tools.getExternalStorage(this.mainActivity) + Tools.generateID() + ".jpg";
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri output = FileProvider.getUriForFile(this.mainActivity, BuildConfig.APPLICATION_ID + ".provider", new File(filename));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                    code = TAKE_PICTURE;
                }
            } else if (v == lytBtnGallery) {
                if (this.permissionsChecker.checkStoragePermission()) {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    code = SELECT_PICTURE;
                }
            }
            if (intent != null && intent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                startActivityForResult(intent, code);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
                image_thumb.setImageBitmap(BitmapFactory.decodeFile(filename));
            } else if (requestCode == SELECT_PICTURE) {
                try {
                    Uri selectedImage = data.getData();
                    InputStream is;
                    is = this.mainActivity.getContentResolver().openInputStream(selectedImage);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    ImageView iv = this.root.findViewById(R.id.image_thumb);
                    iv.setImageBitmap(bitmap);
                    filename = Tools.getRealPathFromURI(this.mainActivity, selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}