package io.chenxi.easyplan.rpc

class ObjectAdapter {
    companion object {
        fun asRpcObjectArray(dates: Array<Any>, rxData: Array<Any>, txData: Array<Any>) {
            val numSamples = dates.size
            if (numSamples != rxData.size || numSamples != txData.size)
                throw IllegalArgumentException("Input arrays should have the same size")


        }
    }
}