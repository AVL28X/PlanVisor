package io.chenxi.easyplan.util

import java.util.Calendar


class TimeUtils {
    companion object {
        fun getTimeTodayStartInMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            return cal.timeInMillis
        }

        fun getTimeMonthStartInMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
            return cal.timeInMillis
        }
    }
}
