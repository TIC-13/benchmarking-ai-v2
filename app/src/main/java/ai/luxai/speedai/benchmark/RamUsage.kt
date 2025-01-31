package ai.luxai.speedai.benchmark

import android.os.Debug

class RamUsage {

    private var usage = 0F
    private var totalUsage = 0F
    private var numSamples = 0
    private var peak = 0F

    fun calculateUsage() {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        val totalPss = memoryInfo.totalPss
        usage = (totalPss / 1024F)
        if(usage > peak) peak = usage
        totalUsage += usage
        numSamples += 1
    }

    fun get(): Int {
        return usage.toInt()
    }

    fun peak(): Float {
        return peak
    }

    fun getAverage(): Float {
        return (totalUsage/numSamples)
    }
}