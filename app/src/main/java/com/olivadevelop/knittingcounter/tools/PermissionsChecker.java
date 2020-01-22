package com.olivadevelop.knittingcounter.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsChecker {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 4;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 5;

    private Activity mainActivity;

    public PermissionsChecker(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean checkCameraPermission() {
        boolean retorno = true;
        if (ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
            // Explicamos porque necesitamos el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.mainActivity, Manifest.permission.CAMERA)) {
                // Acá continuamos el procesos deseado a hacer
            }
            ActivityCompat.requestPermissions(this.mainActivity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
        return retorno;
    }

    public boolean checkStoragePermission() {
        boolean retorno = true;
        if (ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            retorno = false;
            // Explicamos porque necesitamos el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Acá continuamos el procesos deseado a hacer
            }
            ActivityCompat.requestPermissions(this.mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
        }
        if (retorno && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Permiso necesario solo para versiones anteriores a la 19
            if (ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                retorno = false;
                // Explicamos porque necesitamos el permiso
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Acá continuamos el procesos deseado a hacer
                }
                ActivityCompat.requestPermissions(this.mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        }
        return retorno;
    }
}
