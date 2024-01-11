package com.example.newbenchmarking.interfaces

data class InferenceParams(
    var model: Model,
    var useNNAPI: Boolean,
    var useGPU: Boolean,
    var numThreads: Int,
    var numImages: Int
)