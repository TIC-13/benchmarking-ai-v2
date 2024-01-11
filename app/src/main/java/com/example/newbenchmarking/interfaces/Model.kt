package com.example.newbenchmarking.interfaces

data class Model(
    val label: String,
    val filename: String,
    val modelType: ModelType
)

enum class ModelType{ CLASSIFICATION, SEGMENTATION }

val models: List<Model> = listOf(
    Model(
        "Efficientnet FP32",
        "efficientNetFP32.tflite",
        ModelType.CLASSIFICATION
    ),
    Model(
        "Efficientnet INT8",
        "efficientNetINT8.tflite",
        ModelType.CLASSIFICATION
    )
)