package com.example.newbenchmarking.benchmark

import android.os.Debug

class RamUsage {

    private var usage = 0F
    private var totalUsage = 0F
    private var numSamples = 0

    fun calculateUsage() {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        val totalPss = memoryInfo.totalPss
        usage = (totalPss / 1024F)
        totalUsage += usage
        numSamples += 1
    }

    fun get(): Int {
        return usage.toInt()
    }

    fun getAverage(): Float {
        return (totalUsage/numSamples)
    }
}