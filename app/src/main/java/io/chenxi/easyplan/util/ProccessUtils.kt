package io.chenxi.easyplan.util

import android.app.usage.NetworkStats
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.RemoteException


class ProcessUtils {
    companion object {
        fun getUidByPackageName(context: Context, packageName: String): Int {
            var uid = -1
            val packageManager = context.getPackageManager()
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)

                uid = packageInfo.applicationInfo.uid
            } catch (e: PackageManager.NameNotFoundException) {
            }
            return uid
        }


    }
}