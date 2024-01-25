package com.example.newbenchmarking.interfaces

import org.tensorflow.lite.DataType

data class Model(
    val label: String,
    val filename: String,
    val inputShape: IntArray,
    val outputShape: IntArray,
    val inputDataType: DataType,
    val outputDataType: DataType
)

val models: List<Model> = listOf(
    Model(
        "Efficientnet FP32",
        "efficientNetFP32.tflite",
        inputShape = intArrayOf(1, 224, 224, 3),
        outputShape = intArrayOf(1, 1000),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Efficientnet INT8",
        "efficientNetINT8.tflite",
        inputShape = intArrayOf(1, 224, 224, 3),
        outputShape = intArrayOf(1, 1000),
        inputDataType = DataType.UINT8,
        outputDataType = DataType.UINT8
    ),
    Model(
        "DeepLab v3",
        "deeplabv3.tflite",
        inputShape = intArrayOf(1, 257, 257, 3),
        outputShape = intArrayOf(1, 257, 257, 21),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "SSD MobileNet v1 (Detecção de objeto)",
        "ssd_mobilenet_v2.tflite",
        inputShape = intArrayOf(1, 300, 300, 3),
        outputShape = intArrayOf(1, 300, 300, 3),
        inputDataType = DataType.UINT8,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolo v4 - FP32",
        "--yolov4-tiny-416-fp32.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(1, 416, 416, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolo v4 - FP16",
        "--yolov4-tiny-416-fp16.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(1, 416, 416, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolo v4 - INT8",
        "--yolov4-tiny-416-int8.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(1, 416, 416, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Image Super resolution - ESRGAN",
        "esrgan.tflite",
        inputShape = intArrayOf(1, 50, 50, 3),
        outputShape = intArrayOf(1, 200, 200, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Image deblurring - IMDN",
        "imdn_rtc_time.tflite",
        inputShape = intArrayOf(1, 720, 480, 3),
        outputShape = intArrayOf(1, 1440, 960, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    )
)