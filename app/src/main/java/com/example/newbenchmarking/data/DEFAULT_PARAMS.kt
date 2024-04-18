package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult

val DEFAULT_PARAMS = InferenceResult(
    cpuAverage = 0F,
    gpuAverage =  0,
    ramConsumedAverage =  0F,
    inferenceTimeAverage = 0L,
    firstInference = null,
    loadTime = 0L,
    params = InferenceParams(
        model = MODELS[0],
        numImages = DATASETS[0].imagesId.size/4,
        numThreads = 1,
        useGPU = false,
        useNNAPI = false,
        dataset = DATASETS[0]
    )
)