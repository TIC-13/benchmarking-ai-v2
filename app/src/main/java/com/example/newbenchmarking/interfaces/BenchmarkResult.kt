package com.example.newbenchmarking.interfaces

import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.Energy
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage

data class BenchmarkResult(
    val params: InferenceParams,
    val inference: Inference,
    val cpu: CpuUsage,
    val gpu: GpuUsage,
    val ram: RamUsage,
    val energy: Energy,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)