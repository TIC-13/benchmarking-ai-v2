package com.example.newbenchmarking.benchmark

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class CpuUsage {

    private var cpuUsage: Int? = 0
    private var totalCPUUsage = 0
    private var numberOfCPUUsageSamples = 0
    private var peak = 0

    fun calculateCPUUsage(): Int? {

        val processName = "com.example.newbenchmarking"

        val totalCapacityStringBuilder = StringBuilder()
        val processStringBuilder = StringBuilder()

        try {
            val process = Runtime.getRuntime().exec("top -n 1 -b")
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String?
            while (true) {
                line = reader.readLine()
                if (line == null) {
                    break
                }
                if (line.contains("%cpu")) {
                    totalCapacityStringBuilder.append(line)
                }
                if (line.contains(processName)) {
                    processStringBuilder.append(line)
                    break
                }
            }

            process.waitFor()
            process.destroy()

            val processLine = processStringBuilder.toString()
            val cpuUsageParsed = processLine.replace(Regex("\\s+"), " ").split(" ")[9].toFloat().toInt()

            val totalCapacityLine = totalCapacityStringBuilder.toString()
            val totalCapacity = Regex("^\\d+").find(totalCapacityLine)?.value?.toInt() ?: return 0

            val cpuUsagePercentage = cpuUsageParsed*100 / totalCapacity
            cpuUsage = cpuUsagePercentage
            if(cpuUsage!! > peak) peak = cpuUsage!!
            totalCPUUsage += cpuUsagePercentage
            numberOfCPUUsageSamples ++
            return cpuUsagePercentage
        } catch (e: Exception){
            e.printStackTrace()
            cpuUsage = null
            return null
        }
    }

    fun getCPUUsage(): Int? {
        return cpuUsage
    }

    fun peak(): Int {
        return peak
    }

    fun getAverageCPUConsumption(): Int? {
        if(numberOfCPUUsageSamples == 0) return null
        return (totalCPUUsage/numberOfCPUUsageSamples)
    }
}