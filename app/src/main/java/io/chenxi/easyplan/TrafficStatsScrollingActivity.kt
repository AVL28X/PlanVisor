package io.chenxi.easyplan

import android.Manifest
import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import android.telephony.TelephonyManager
import android.util.Log
import io.chenxi.easyplan.util.DataUtils
import io.chenxi.easyplan.util.PermissionUtils
import io.chenxi.easyplan.util.TimeUtils
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.view.LineChartView
import java.text.SimpleDateFormat
import java.util.*


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


                var bucket: NetworkStats.Bucket?
                val dayStart = TimeUtils.getTimeTodayStartInMillis()
                val monthStart = TimeUtils.getTimeMonthStartInMillis()
                bucket = networkStatsManager?.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, getSubscriberId(this, ConnectivityManager.TYPE_MOBILE), dayStart, System.currentTimeMillis())
                Log.i("Info", "Daily Use ${DataUtils.convertByteToMB(bucket!!.rxBytes + bucket.txBytes)}")
                bucket = networkStatsManager?.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, getSubscriberId(this, ConnectivityManager.TYPE_MOBILE), monthStart, System.currentTimeMillis())
                Log.i("Info", "Monthly Use ${DataUtils.convertByteToMB(bucket!!.rxBytes + bucket.txBytes)}")
                val timeIntervals = TimeUtils.getDateIntervalsInMillis()
                val rxUsageByDay = ArrayList<Float>()
                val txUsageByDay = ArrayList<Float>()
                val dates = ArrayList<String>()
                val subscriberId = getSubscriberId(this, ConnectivityManager.TYPE_MOBILE)
                for (i in 1 until timeIntervals.size) {
                    bucket = networkStatsManager?.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                            subscriberId, timeIntervals[i - 1], timeIntervals[i])
                    dates.add(SimpleDateFormat("MM/dd/yyyy").format(Date(timeIntervals[i - 1])))
                    rxUsageByDay.add(DataUtils.convertByteToMB(bucket!!.rxBytes))
                    txUsageByDay.add(DataUtils.convertByteToMB(bucket.txBytes))
                }
                Snackbar.make(view, "Data Received ${rxUsageByDay}MB and sent ${txUsageByDay.sum()}MB ${dates[0]}", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()




                Log.i("Usage", "$dates")
                Log.i("Usage", "$rxUsageByDay")
                Log.i("Usage", "$txUsageByDay")



            }
        }


        val values = ArrayList<PointValue>()
        values.add(PointValue(0f, 2f))
        values.add(PointValue(1f, 4f))
        values.add(PointValue(2f, 3f))
        values.add(PointValue(3f, 4f))
        values.add(PointValue(3f, 4f))
        values.add(PointValue(3f, 4f))
        values.add(PointValue(3f, 4f))

        //In most cased you can call data model methods in builder-pattern-like manner.
        val line = Line(values).setColor(Color.BLUE).setCubic(true)
        val lines = ArrayList<Line>()
        lines.add(line)

        val data = LineChartData()
        data.setLines(lines)

        val chart = LineChartView(this)
        chart.setLineChartData(data)


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
