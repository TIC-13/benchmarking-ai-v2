package com.example.newbenchmarking.interfaces

data class InferenceResult(
    val cpuAverage: Int? = null,
    val cpuPeak: Int? = null,
    val gpuAverage: Int? = null,
    val gpuPeak: Int? = null,
    val ramConsumedAverage: Float? = null,
    val ramPeak: Float? = null,
    val inferenceTimeAverage: Long? = null,
    val loadTime: Long? = null,
    val firstInference: Long? = null,
    val standardDeviation: Double? = null,
    val params: InferenceParams,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)