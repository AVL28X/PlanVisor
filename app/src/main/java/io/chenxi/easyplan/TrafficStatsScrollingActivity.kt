package io.chenxi.easyplan

import android.Manifest
import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.system.Os
import android.telephony.TelephonyManager
import android.util.Log
import io.chenxi.easyplan.util.DataUtils
import io.chenxi.easyplan.util.PermissionUtils
import io.chenxi.easyplan.util.TimeUtils


class TrafficStatsScrollingActivity : AppCompatActivity() {

    private var networkStatsManager: NetworkStatsManager? = null

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traffic_stats_scrolling)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        hasPermissionToReadNetworkStats()
        PermissionUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            run {
                networkStatsManager = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

                Snackbar.make(view, "Updating" + Os.getuid(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

                var bucket: NetworkStats.Bucket?
                val dayStart = TimeUtils.getTimeTodayStartInMillis()
                val monthStart = TimeUtils.getTimeMonthStartInMillis()
                bucket = networkStatsManager?.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, getSubscriberId(this, ConnectivityManager.TYPE_MOBILE), dayStart, System.currentTimeMillis())
                Log.i("Info", "Daily Use ${DataUtils.convertByteToMB(bucket!!.rxBytes + bucket.txBytes)}")
                bucket = networkStatsManager?.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, getSubscriberId(this, ConnectivityManager.TYPE_MOBILE), monthStart, System.currentTimeMillis())
                Log.i("Info", "Monthly Use ${DataUtils.convertByteToMB(bucket!!.rxBytes + bucket.txBytes)}")


            }
        }
    }


    private fun hasPermissionToReadNetworkStats(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), packageName)
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true
        }

        requestReadNetworkStats()
        return false
    }

    private fun requestReadNetworkStats() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }


    fun getPackageRxBytesMobile(context: Context): Long {
        var networkStats: NetworkStats? = null
        try {
            networkStats = networkStatsManager?.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis(),
                    123)
        } catch (e: RemoteException) {
            return -1
        }

        val bucket = NetworkStats.Bucket()
        networkStats!!.getNextBucket(bucket)
        networkStats.getNextBucket(bucket)
        return bucket.rxBytes
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getPackageTxBytesMobile(context: Context): Long {
        var networkStats: NetworkStats?
        try {
            networkStats = networkStatsManager?.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis(),
                    123)
        } catch (e: RemoteException) {
            return -1
        }

        val bucket = NetworkStats.Bucket()
        networkStats!!.getNextBucket(bucket)
        return bucket.txBytes
    }

    private fun getSubscriberId(context: Context, networkType: Int): String {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.subscriberId
        }
        return ""
    }

}
