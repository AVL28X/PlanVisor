package io.chenxi.easyplan.util

import java.util.*
import kotlin.collections.ArrayList


class TimeUtils {
    companion object {
        val NUM_MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L

        fun getTimeTodayStartInMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            return cal.timeInMillis
        }

        fun getTimeMonthStartInMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
            return cal.timeInMillis - 30 * NUM_MILLISECONDS_PER_DAY
        }

        fun getDateIntervalsInMillis(): ArrayList<Long> {
            val cal = Calendar.getInstance()
            val intervals = ArrayList<Long>()
            val currentTimeInMillis = cal.timeInMillis
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
            var iterTimeInMillis = cal.timeInMillis
            while (iterTimeInMillis < currentTimeInMillis) {
                intervals.add(iterTimeInMillis)
                iterTimeInMillis += NUM_MILLISECONDS_PER_DAY
            }
            intervals.add(currentTimeInMillis)
            return intervals
        }

    }
}
