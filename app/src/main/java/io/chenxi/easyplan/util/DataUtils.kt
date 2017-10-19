package io.chenxi.easyplan.util


class DataUtils {
    companion object {
        fun convertByteToMB(dataInByte: Long): Float {
            return dataInByte.toFloat() / 1024 / 1024
        }
    }
}
