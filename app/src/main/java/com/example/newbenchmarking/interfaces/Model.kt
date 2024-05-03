package com.example.newbenchmarking.interfaces

import org.tensorflow.lite.DataType

data class Model(
    val label: String,
    val description: String,
    val longDescription: String,
    val filename: String,
    var quantization: Quantization,
    val inputShape: IntArray? = null,
    val outputShape: IntArray? = null,
    val inputDataType: DataType? = null,
    val outputDataType: DataType? = null,
    var category: Category
)

enum class Quantization {
    FP32, FP16, INT8, INT32
}

enum class Category {
    CLASSIFICATION, DETECTION, SEGMENTATION, IMAGE_DEBLURRING, IMAGE_SUPER_RESOLUTION, BERT
}

