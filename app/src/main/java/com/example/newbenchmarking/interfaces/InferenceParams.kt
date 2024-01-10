package com.example.newbenchmarking.interfaces

data class InferenceParams(
    var modelFile: String?,
    var useNNAPI: Boolean,
    var numImages: Int
)