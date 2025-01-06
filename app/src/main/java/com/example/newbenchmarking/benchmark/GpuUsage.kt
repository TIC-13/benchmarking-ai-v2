package com.example.newbenchmarking.benchmark

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

class GpuUsage {

    private var usage: Int? = 0
    private var totalUsage = 0
    private var numSamples = 0
    private var peak = 0

    private fun readFile(filePath: String): String {
        return File(filePath).readText().trim()
    }

    private fun usageMali(): Int? {
        val maliFilePath = "/sys/kernel/gpu/gpu_busy"
        val maliFileContent = readFile(maliFilePath)

        if(maliFileContent == "")
            return null

        return maliFileContent.dropLast(1).toInt()
    }

    private fun usageAdreno(): Int? {
        val adrenoFilePath = "/sys/class/kgsl/kgsl-3d0/gpubusy"
        val adrenoFileContent = readFile(adrenoFilePath)

        if(adrenoFileContent == "")
            return null

        val pair = adrenoFileContent
            .replace("\n", "")
            .split(" ")
            .map {x -> x.trim().toInt()}

        if(pair[1] == 0)
            return null

        if(pair[0] == 0)
            return 0

        return ((pair[0].toFloat()/pair[1].toFloat())*100).toInt()
    }

    private fun runFallbackOnFail(getValue: () -> Int?, fallback: () -> Int?): Int? {
        return try {
            getValue()
        }catch (e: Exception){
            fallback()
        }
    }

    fun calculateUsage() {

        usage = runFallbackOnFail(::usageMali) {
            runFallbackOnFail(::usageAdreno) {
                null
            }
        }

        if(usage == null)
            return

        if(usage!! > peak) peak = usage!!
        totalUsage += usage!!
        numSamples += 1

    }

    fun peak(): Int {
        return peak
    }

    fun get(): Int? {
        return usage
    }

    fun getAverage(): Int {
        return (totalUsage.toFloat()/numSamples).toInt()
    }

}