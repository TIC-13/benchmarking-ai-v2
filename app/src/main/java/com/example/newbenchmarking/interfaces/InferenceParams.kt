package com.example.newbenchmarking.interfaces

data class InferenceParams(
    var model: Model,
    var useNNAPI: Boolean,
    var numImages: Int
)