package com.olivadevelop.knittingcounter.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.olivadevelop.knittingcounter.BuildConfig;
import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;
import com.olivadevelop.knittingcounter.db.ProjectController;
import com.olivadevelop.knittingcounter.tools.PermissionsChecker;
import com.olivadevelop.knittingcounter.tools.Tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class ToolsProject {

    private PermissionsChecker permissionsChecker;
    private MainActivity mainActivity;
    private Fragment fragment;
    private View root;
    private String currentPhotoPath;

    ToolsProject(Fragment fragment, View root) {
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.root = root;
        this.fragment = fragment;
        this.permissionsChecker = new PermissionsChecker(this.mainActivity);
        if (this.mainActivity != null) {
            this.permissionsChecker.checkStoragePermission();
            this.permissionsChecker.checkCameraPermission();
        }
    }

    void takePhotoFromCamera(String projectName) throws IOException {
        this.mainActivity.hideImputMedia(this.root);
        if (this.permissionsChecker.checkStoragePermission() && this.permissionsChecker.checkCameraPermission()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                File photoFile = createImageFile(projectName);
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this.root.getContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile
                    );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    this.fragment.startActivityForResult(takePictureIntent, ProjectController.TAKE_PICTURE);
                }
            }
        } else {
            Snackbar customSnackbar = this.mainActivity.customSnackBar(this.root, R.string.error_permissions_new_project, R.drawable.ic_warning_black_18dp).setAction(R.string.btn_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    permissionsChecker.checkStoragePermission();
                    permissionsChecker.checkCameraPermission();
                }
            });
            customSnackbar.show();
            this.mainActivity.setCustomSnackbar(customSnackbar);
        }
    }

    void takePhotoFromGallery() {
        this.mainActivity.hideImputMedia(this.root);
        if (this.permissionsChecker.checkStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            if (intent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                this.fragment.startActivityForResult(intent, ProjectController.SELECT_PICTURE);
            }
        }
    }

    Bitmap resultFromTakePhotoFromCamera(Intent data) throws IOException {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap;
        if (extras == null) {
            File file = new File(this.currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            imageBitmap = MediaStore.Images.Media.getBitmap(this.mainActivity.getContentResolver(), uri);
        } else {
            imageBitmap = (Bitmap) extras.get(MediaStore.EXTRA_OUTPUT);
        }
        return imageBitmap;
    }

    Bitmap resultFromTakePhotoFromGallery(Intent data) throws IOException {
        Bitmap bitmap = null;
        Uri selectedImage = data.getData();
        if (selectedImage != null) {
            InputStream is = this.mainActivity.getContentResolver().openInputStream(selectedImage);
            if (is != null) {
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
                setCurrentPhotoPath(Tools.getRealPathFromURI(this.mainActivity, selectedImage));
                is.close();
            }
        }
        return bitmap;
    }

    private File createImageFile(String projectName) throws IOException {
        // Create an image file name
        String imageFileName = "header_" + projectName;
        File storageDir = this.mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        setCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

    String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }
}
