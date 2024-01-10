package com.example.newbenchmarking.interfaces

data class InferenceResult(
    val cpuAverage: Float,
    val gpuAverage: Float,
    val ramConsumedAverage: Float,
    val inferenceTimeAverage: Long,
    val loadTime: Long,
)