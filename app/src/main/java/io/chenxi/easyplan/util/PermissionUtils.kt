package io.chenxi.easyplan.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

class PermissionUtils {
    companion object {
        //Manifest.permission.READ_PHONE_STATE
        fun checkPermission(context: Activity, permission: String): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, arrayOf(permission), 0);
            }
            return true
        }
    }
}
