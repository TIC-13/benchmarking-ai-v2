package com.example.newbenchmarking.benchmark

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class CpuUsage {

    private var cpuUsage = 0F
    private var totalCPUUsage = 0F
    private var numberOfCPUUsageSamples = 0L

    fun calculateCPUUsage(): Float {

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
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val processLine = processStringBuilder.toString()
        val cpuUsageParsed = processLine.replace(Regex("\\s+"), " ").split(" ")[9].toFloat()

        val totalCapacityLine = totalCapacityStringBuilder.toString()
        val totalCapacity = Regex("^\\d+").find(totalCapacityLine)?.value?.toInt() ?: return 0F

        val cpuUsagePercentage = cpuUsageParsed / totalCapacity
        cpuUsage = cpuUsagePercentage
        totalCPUUsage += cpuUsagePercentage
        numberOfCPUUsageSamples ++
        return cpuUsagePercentage
    }

    fun getCPUUsage(): Float {
        return cpuUsage * 100
    }

    fun getAverageCPUConsumption(): Float {
        return (totalCPUUsage/numberOfCPUUsageSamples) * 100
    }
}