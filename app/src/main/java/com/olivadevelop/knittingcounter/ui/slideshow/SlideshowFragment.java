package com.olivadevelop.knittingcounter.ui.slideshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.BuildConfig;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ProjectController;
import com.olivadevelop.knittingcounter.model.Project;
import com.olivadevelop.knittingcounter.tools.PermissionsChecker;
import com.olivadevelop.knittingcounter.tools.Tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private MainActivity mainActivity;
    private View root;
    private EditText projectName;
    private EditText projectNeedleNum;
    private LinearLayout lytBtnCamera;
    private LinearLayout lytBtnGallery;
    private ImageView image_thumb;

    private String currentPhotoPath;
    private PermissionsChecker permissionsChecker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.new_project, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save_project) {
            createProject();
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        this.projectName = this.root.findViewById(R.id.project_name);
        this.projectNeedleNum = this.root.findViewById(R.id.project_needle);

        this.lytBtnCamera = this.root.findViewById(R.id.btnCamera);
        this.lytBtnGallery = this.root.findViewById(R.id.btnGalery);
        this.image_thumb = this.root.findViewById(R.id.image_thumb);

        this.lytBtnCamera.setOnClickListener(this);
        this.lytBtnGallery.setOnClickListener(this);

        this.permissionsChecker.checkStoragePermission();
        this.permissionsChecker.checkCameraPermission();

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
        this.mainActivity.hideImputMedia(this.root);
        this.mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == lytBtnCamera) {
                this.mainActivity.hideImputMedia(this.root);
                if (this.permissionsChecker.checkStoragePermission() && this.permissionsChecker.checkCameraPermission()) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                        File photoFile = createImageFile();
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(this.root.getContext(),
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    photoFile
                            );
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, TAKE_PICTURE);
                        }
                    }
                } else {
                    this.mainActivity.customSnackBar(this.root, R.string.error_permissions_new_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionsChecker.checkStoragePermission();
                            permissionsChecker.checkCameraPermission();
                        }
                    }).show();
                }
            } else if (v == lytBtnGallery) {
                this.mainActivity.hideImputMedia(this.root);
                if (this.permissionsChecker.checkStoragePermission()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    if (intent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                        startActivityForResult(intent, SELECT_PICTURE);
                    }
                }
            }
        } catch (Exception e) {
            this.mainActivity.customSnackBar(this.root, R.string.error_image_new_project, R.drawable.ic_warning_black_18dp).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == TAKE_PICTURE) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap;
                    if (extras == null) {
                        File file = new File(currentPhotoPath);
                        Uri uri = Uri.fromFile(file);
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.mainActivity.getContentResolver(), uri);
                    } else {
                        imageBitmap = (Bitmap) extras.get(MediaStore.EXTRA_OUTPUT);
                    }
                    image_thumb.setImageBitmap(imageBitmap);
                } else if (requestCode == SELECT_PICTURE) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        InputStream is = this.mainActivity.getContentResolver().openInputStream(selectedImage);
                        if (is != null) {
                            BufferedInputStream bis = new BufferedInputStream(is);
                            Bitmap bitmap = BitmapFactory.decodeStream(bis);
                            ImageView iv = this.root.findViewById(R.id.image_thumb);
                            iv.setImageBitmap(bitmap);
                            this.currentPhotoPath = Tools.getRealPathFromURI(this.mainActivity, selectedImage);
                            is.close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.mainActivity.customSnackBar(this.root, R.string.error_image_new_project,
                        R.drawable.ic_warning_black_18dp).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "header_" + this.projectName.getText();
        File storageDir = this.mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void createProject() {
        this.mainActivity.hideImputMedia(this.root);
        if (!Tools.isNotEmpty(this.projectName.getText()) && !Tools.isNotEmpty(this.projectNeedleNum.getText())) {
            this.mainActivity.customSnackBar(this.root, R.string.error_new_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createProject();
                }
            }).show();
            return;
        }
        Project pExists = ProjectController.getInstance().findByName(this.mainActivity, this.projectName.getText().toString());
        if (pExists != null) {
            this.mainActivity.customSnackBar(this.root, R.string.error_new_project_already_exists, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_clean, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetForm();
                }
            }).show();
            return;
        }

        Project p = new Project();
        p.setName(this.projectName.getText().toString());
        p.setHeaderImgUri(this.currentPhotoPath);
        p.setNeedleNum(Float.valueOf(this.projectNeedleNum.getText().toString()));
        long idNew = ProjectController.getInstance().create(this.mainActivity, p);

        if (idNew > 0) {
            resetForm();
            this.mainActivity.customSnackBar(this.root, R.string.label_new_project_ok, R.drawable.ic_done_black_18dp).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(root).navigate(R.id.action_nav_slideshow_to_nav_home);
                }
            }).show();
        } else {
            this.mainActivity.customSnackBar(this.root, R.string.error_new_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createProject();
                }
            }).show();
        }
    }

    private void resetForm() {
        this.currentPhotoPath = null;
        this.projectName.setText("");
        this.projectNeedleNum.setText("");
        this.image_thumb.setImageDrawable(getResources().getDrawable(R.drawable.ic_crop_free_black_24dp));
    }
}