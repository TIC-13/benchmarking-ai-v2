package com.example.newbenchmarking.benchmark

import java.io.File
import java.io.FileNotFoundException

class GpuUsage {

    private var usage = 0
    private var totalUsage = 0
    private var numSamples = 0

    fun calculateUsage() {
        val filePath = "/sys/kernel/gpu/gpu_busy"
        try {
            val percentString = File(filePath).readText().trim()
            usage = percentString.dropLast(1).toInt()
        } catch (e: FileNotFoundException) {
            println("File not found: $filePath")
            usage = 0
        } catch (e: Exception) {
            println("Error reading file: ${e.message}")
            usage = 1
        }
        totalUsage += usage
        numSamples += 1
    }

    fun get(): Int {
        return usage
    }

    fun getAverage(): Int {
        return (totalUsage.toFloat()/numSamples).toInt()
    }

}