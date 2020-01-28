package com.olivadevelop.knittingcounter.tools;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
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
import com.olivadevelop.knittingcounter.tools.PermissionsChecker;
import com.olivadevelop.knittingcounter.tools.Tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class ToolsProject {

    public static int TAKE_PICTURE = 1;
    public static int SELECT_PICTURE = 2;

    private static final float THUMBNAIL_SIZE = 300;
    private PermissionsChecker permissionsChecker;
    private MainActivity mainActivity;
    private Fragment fragment;
    private View root;
    private String currentPhotoPath;

    public ToolsProject(Fragment fragment, View root) {
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.root = root;
        this.fragment = fragment;
        this.permissionsChecker = new PermissionsChecker(this.mainActivity);
        if (this.mainActivity != null) {
            this.permissionsChecker.checkStoragePermission();
            this.permissionsChecker.checkCameraPermission();
        }
    }

    public void takePhotoFromCamera(String projectName) throws IOException {
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
                    this.fragment.startActivityForResult(takePictureIntent, TAKE_PICTURE);
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

    public void takePhotoFromGallery() {
        this.mainActivity.hideImputMedia(this.root);
        if (this.permissionsChecker.checkStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            if (intent.resolveActivity(this.mainActivity.getPackageManager()) != null) {
                this.fragment.startActivityForResult(intent, SELECT_PICTURE);
            }
        }
    }

    public Bitmap resultFromTakePhotoFromCamera(Intent data) throws IOException {
        Bitmap imageBitmap;
        if (data == null || data.getExtras() == null) {
            File file = new File(this.currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.mainActivity.getContentResolver(), uri));
            } else {
                imageBitmap = getBitmap(this.mainActivity.getContentResolver(), uri);
            }
        } else {
            imageBitmap = (Bitmap) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
        }
        return imageBitmap;
    }

    public Bitmap resultFromTakePhotoFromGallery(Intent data) throws IOException {
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

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.mainActivity.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        if (input != null) {
            input.close();
        }

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.mainActivity.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        if (input != null) {
            input.close();
        }
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
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

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }
}
