package com.example.newbenchmarking.interfaces

import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class BenchmarkResult(
    val params: InferenceParams,
    val inference: Inference,
    val cpu: CpuUsage,
    val gpu: GpuUsage,
    val ram: RamUsage,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)

val gson = Gson()

fun BenchmarkResult.toJson(): String {
    return gson.toJson(this)
}

fun String.toBenchmarkResult(): BenchmarkResult {
    val type = object : TypeToken<BenchmarkResult>() {}.type
    return gson.fromJson(this, type)
}