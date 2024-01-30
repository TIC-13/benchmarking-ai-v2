package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.InferenceParams

val BENCHMARKING_TESTS = arrayListOf(
    InferenceParams(
        model = MODELS[0],
        useNNAPI = false,
        useGPU = false,
        numThreads = 1,
        numImages = 400,
        dataset = DATASETS[0]
    ),
    InferenceParams(
        model = MODELS[0],
        useNNAPI = false,
        useGPU = false,
        numThreads = 1,
        numImages = 5,
        dataset = DATASETS[1]
    ),
)