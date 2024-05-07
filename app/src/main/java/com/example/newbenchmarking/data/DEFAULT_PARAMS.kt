package com.example.newbenchmarking.data

import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization

val DEFAULT_PARAMS = BenchmarkResult(
        cpu = CpuUsage(),
        ram = RamUsage(),
        gpu = GpuUsage(),
        inference = Inference(
            load = null,
            average = null,
            first = null,
            standardDeviation = null
        ),
        params = InferenceParams(
            model = Model(
                "Efficientnet",
                "Classificação de imagem",
                "O EfficientNet é um modelo de machine learning otimizado para classificação de imagens. Sua arquitetura eficiente e escalável o torna versátil para lidar com uma variedade de desafios em visão computacional.",
                "efficientNetFP32.tflite",
                category = Category.CLASSIFICATION,
                quantization = Quantization.FP32,
            ),
            numImages = DATASETS[0].imagesId.size/4,
            numThreads = 1,
            useGPU = false,
            useNNAPI = false,
            dataset = DATASETS[0]
        )
)