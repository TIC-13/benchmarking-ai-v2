package com.example.newbenchmarking.benchmark

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

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
            println("Erro na leitura do arquivo em Mali: $filePath")
            usage = usageAdreno().toInt()
        } catch (e: Exception) {
            println("Error reading file: ${e.message}")
            usage = 0
        }
        totalUsage += usage
        numSamples += 1
    }

    private fun usageAdreno(): Float {
        val filePath = "/sys/class/kgsl/kgsl-3d0/gpubusy"
        try {
            val process = Runtime.getRuntime().exec("cat $filePath")
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText().trim()
            if(output.startsWith("0"))
                return 0F
            val pair = output
                .replace("\n", "")
                .split(" ")
                .map {x -> x.trim().toFloat()}
            if(pair[0] == 0F || pair[1] == 0F)
                return 0F
            return (pair[0]/pair[1])*100
        }catch(e: FileNotFoundException){
            println("Erro na leitura do arquivo em Adreno: $filePath")
            return 0F
        }catch(e: Exception){
            println("Outro error em leitura adreno: " + e.message)
            return 0F
        }
    }

    fun get(): Int {
        return usage
    }

    fun getAverage(): Int {
        return (totalUsage.toFloat()/numSamples).toInt()
    }

}