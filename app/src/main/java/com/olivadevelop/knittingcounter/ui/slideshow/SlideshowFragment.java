package com.olivadevelop.knittingcounter.ui.slideshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.BuildConfig;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ManageDatabase;
import com.olivadevelop.knittingcounter.tools.PermissionsChecker;
import com.olivadevelop.knittingcounter.tools.Tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private String currentPhotoPath;
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
        try {
            if (v == btnCreate) {
                ManageDatabase md = new ManageDatabase(this.getContext(), false);
                int id = md.count(ManageDatabase.TABLE_PROJECTS);
                md.insert(ManageDatabase.TABLE_PROJECTS,
                        new String[]{"_id", "name", "creation_date", "lap", "needle_num", "header_img_uri"},
                        new String[]{String.valueOf(id), this.projectName.getText().toString(), Tools.formatDate(new Date()), String.valueOf(0d), this.projectNeedleNum.getText().toString(), this.currentPhotoPath}
                );
                md.closeDB();
            } else {
                if (v == lytBtnCamera) {
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
                    }
                } else if (v == lytBtnGallery) {
                    if (this.permissionsChecker.checkStoragePermission()) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        if (intent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                            startActivityForResult(intent, SELECT_PICTURE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            this.mainActivity.CustomSnackBar(this.root, R.string.error_image_new_project,
                    R.drawable.ic_warning_black_18dp).show();
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
                        //imageBitmap = cropAndScale(imageBitmap, 300); // if you mind scaling
                    } else {
                        imageBitmap = (Bitmap) extras.get(MediaStore.EXTRA_OUTPUT);
                    }
                    image_thumb.setImageBitmap(imageBitmap);
                    //image_thumb.setImageBitmap(BitmapFactory.decodeFile(filename));
                } else if (requestCode == SELECT_PICTURE) {
                    Uri selectedImage = data.getData();
                    InputStream is;
                    is = this.mainActivity.getContentResolver().openInputStream(selectedImage);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    ImageView iv = this.root.findViewById(R.id.image_thumb);
                    iv.setImageBitmap(bitmap);
                    this.currentPhotoPath = Tools.getRealPathFromURI(this.mainActivity, selectedImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.mainActivity.CustomSnackBar(this.root, R.string.error_image_new_project,
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
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private static Bitmap cropAndScale(Bitmap source, int scale) {
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight() : source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight() : source.getWidth();
        int x = source.getHeight() >= source.getWidth() ? 0 : (longer - factor) / 2;
        int y = source.getHeight() <= source.getWidth() ? 0 : (longer - factor) / 2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }
}