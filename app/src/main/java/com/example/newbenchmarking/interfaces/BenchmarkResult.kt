package com.example.newbenchmarking.interfaces

data class BenchmarkResult(
    val params: InferenceParams,
    val inference: Inference,
    val cpuAverage: Int? = null,
    val cpuPeak: Int? = null,
    val gpuAverage: Int? = null,
    val gpuPeak: Int? = null,
    val ramConsumedAverage: Float? = null,
    val ramPeak: Float? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)