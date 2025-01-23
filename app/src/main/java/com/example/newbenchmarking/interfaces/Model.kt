package com.example.newbenchmarking.interfaces

import org.tensorflow.lite.DataType
import java.io.File

data class Model(
    val label: String,
    val id: Int,
    val description: String? = null,
    val longDescription: String? = null,
    //val filename: String,
    val file: File,
    var quantization: Quantization? = null,
    val inputShape: IntArray? = null,
    val outputShape: IntArray? = null,
    val inputDataType: DataType? = null,
    val outputDataType: DataType? = null,
    var category: Category? = null
)

enum class Quantization {
    FP32, FP16, INT8, INT32
}

enum class Category {
    CLASSIFICATION, DETECTION, SEGMENTATION, IMAGE_DEBLURRING, IMAGE_SUPER_RESOLUTION, BERT
}

