package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.BenchmarkResult

val DEFAULT_PARAMS = BenchmarkResult(
        cpuAverage = 0,
        gpuAverage =  0,
        ramConsumedAverage =  0F,
        inference = Inference(
            load = null,
            average = null,
            first = null,
            standardDeviation = null
        ),
        params = InferenceParams(
            model = MODELS[0],
            numImages = DATASETS[0].imagesId.size/4,
            numThreads = 1,
            useGPU = false,
            useNNAPI = false,
            dataset = DATASETS[0]
        )
)