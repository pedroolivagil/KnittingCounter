package com.olivadevelop.knittingcounter.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsChecker {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 3;

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
                retorno = true;
            } else {
                // El usuario no necesitas explicación, puedes solicitar el permiso:
                ActivityCompat.requestPermissions(this.mainActivity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                retorno = true;
            }
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
                retorno = true;
            } else {
                // El usuario no necesitas explicación, puedes solicitar el permiso:
                ActivityCompat.requestPermissions(this.mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
                retorno = true;
            }
        }
        if (retorno) {
            if (ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                retorno = false;
                // Explicamos porque necesitamos el permiso
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Acá continuamos el procesos deseado a hacer
                    retorno = true;
                } else {
                    // El usuario no necesitas explicación, puedes solicitar el permiso:
                    ActivityCompat.requestPermissions(this.mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    retorno = true;
                }
            }
        }
        return retorno;
    }
}
