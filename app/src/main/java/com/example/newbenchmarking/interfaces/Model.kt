package com.example.newbenchmarking.interfaces

import org.tensorflow.lite.DataType

data class Model(
    val label: String,
    val description: String,
    val filename: String,
    val inputShape: IntArray,
    val outputShape: IntArray,
    val inputDataType: DataType,
    val outputDataType: DataType
)

