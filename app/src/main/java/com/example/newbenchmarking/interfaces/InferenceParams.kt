package com.example.newbenchmarking.interfaces

data class InferenceParams(
    var model: Model,
    var runMode: RunMode,
    var numThreads: Int,
    var numImages: Int,
    var dataset: Dataset,
    )

enum class RunMode {
    CPU, GPU, NNAPI
}