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
            println("Erro na leitura do arquivo em Mali: $filePath")
            usage = usageAdreno()
        } catch (e: Exception) {
            println("Error reading file: ${e.message}")
            usage = 1
        }
        totalUsage += usage
        numSamples += 1
    }

    private fun usageAdreno(): Int {
        val filePath = "/sys/class/kgsl/kgsl-3d0/gpubusy"
        try {
            val pair = File(filePath).readText().trim().split(" ")
            return (pair[0].toInt()/pair[1].toInt())*100
        }catch(e: FileNotFoundException){
            println("Erro na leitura do arquivo em Adreno: $filePath")
            return 0
        }catch(e: Exception){
            println("Outro error")
            return 0
        }
    }

    fun get(): Int {
        return usage
    }

    fun getAverage(): Int {
        return (totalUsage.toFloat()/numSamples).toInt()
    }

}