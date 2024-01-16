package com.example.newbenchmarking.interfaces

data class Model(
    val label: String,
    val filename: String,
    val modelType: ModelType
)

enum class ModelType{ CLASSIFICATION, SEGMENTATION, DETECTION }

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
    ),
    Model(
        "DeepLab v3",
        "deeplabv3.tflite",
        ModelType.SEGMENTATION
    ),
    Model(
        "SSD MobileNet v1 (Detecção de objeto)",
        "ssd_mobilenet_v2.tflite",
        ModelType.DETECTION
    )
)