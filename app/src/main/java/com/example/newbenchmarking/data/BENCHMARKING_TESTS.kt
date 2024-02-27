package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.InferenceParams

val BENCHMARKING_TESTS: List<InferenceParams> = MODELS.map {
    if(it.label.startsWith("Yolo"))
        listOf(
            InferenceParams(it, useNNAPI = false, useGPU = false, numThreads = 1, numImages = 400, DATASETS[0]),
        )
    else if(it.label.startsWith("ESRGAN") || it.label.startsWith("IMDN"))
        listOf(
            InferenceParams(it, useNNAPI = false, useGPU = false, numThreads = 1, numImages = 100, DATASETS[0]),
            InferenceParams(it, useNNAPI = true, useGPU = false, numThreads = 1, numImages = 100, DATASETS[0]),
            InferenceParams(it, useNNAPI = false, useGPU = true, numThreads = 1, numImages = 100, DATASETS[0]),
        )
    else
        listOf(
            InferenceParams(it, useNNAPI = false, useGPU = false, numThreads = 1, numImages = 400, DATASETS[0]),
            InferenceParams(it, useNNAPI = true, useGPU = false, numThreads = 1, numImages = 400, DATASETS[0]),
            InferenceParams(it, useNNAPI = false, useGPU = true, numThreads = 1, numImages = 400, DATASETS[0]),
            //InferenceParams(it, useNNAPI = true, useGPU = true, numThreads = 1, numImages = 30, DATASETS[0]),
        )
}.flatten()
