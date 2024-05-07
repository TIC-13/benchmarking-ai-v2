package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization

fun getBenchmarkingTests(models: List<Model>): List<InferenceParams> {
    return models.map {
        if(it.label.startsWith("ESRGAN") || it.label.startsWith("IMDN"))
            listOf(
                InferenceParams(it, useNNAPI = false, useGPU = false, numThreads = 1, numImages = 5, DATASETS[0]),
                InferenceParams(it, useNNAPI = true, useGPU = false, numThreads = 1, numImages = 5, DATASETS[0]),
                InferenceParams(it, useNNAPI = false, useGPU = true, numThreads = 1, numImages = 5, DATASETS[0]),
            )
        else
            listOf(
                InferenceParams(it, useNNAPI = false, useGPU = false, numThreads = 1, numImages = 40, DATASETS[0]),
                InferenceParams(it, useNNAPI = true, useGPU = false, numThreads = 1, numImages = 40, DATASETS[0]),
                InferenceParams(it, useNNAPI = false, useGPU = true, numThreads = 1, numImages = 40, DATASETS[0])
            )
    }.flatten()
}


