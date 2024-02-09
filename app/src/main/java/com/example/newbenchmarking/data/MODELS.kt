package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.Model
import org.tensorflow.lite.DataType

val MODELS: List<Model> = listOf(
    Model(
        "Efficientnet FP32",
        "Classificação de imagem",
        "O EfficientNet é um modelo de machine learning otimizado para classificação de imagens. Sua arquitetura eficiente e escalável o torna versátil para lidar com uma variedade de desafios em visão computacional.",
        "efficientNetFP32.tflite",
        inputShape = intArrayOf(1, 224, 224, 3),
        outputShape = intArrayOf(1, 1000),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Efficientnet INT8",
        "Classificação de imagem",
        "hello.",
        "efficientNetINT8.tflite",

        inputShape = intArrayOf(1, 224, 224, 3),
        outputShape = intArrayOf(1, 1000),
        inputDataType = DataType.UINT8,
        outputDataType = DataType.UINT8
    ),
    Model(
        "DeepLab v3",
        "Segmentação de imagem",
        "hello.",
        "deeplabv3.tflite",
        inputShape = intArrayOf(1, 257, 257, 3),
        outputShape = intArrayOf(1, 257, 257, 21),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "SSD MobileNet v1",
        "Detecção de objeto",
        "hello.",
        "ssd_mobilenet_v2.tflite",
        inputShape = intArrayOf(1, 300, 300, 3),
        outputShape = intArrayOf(1, 300, 300, 3),
        inputDataType = DataType.UINT8,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolo v4 - FP32",
        "Detecção de objeto",
        "hello.",
        "--yolov4-tiny-416-fp32.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(1, 416, 416, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolo v4 - FP16",
        "Detecção de objeto",
        "hello.",
        "--yolov4-tiny-416-fp16.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(1, 416, 416, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolo v4 - INT8",
        "Detecção de objeto",
        "hello.",
        "--yolov4-tiny-416-int8.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(1, 416, 416, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "ESRGAN",
        "Aumenta resolução de imagens",
        "hello.",
        "esrgan.tflite",
        inputShape = intArrayOf(1, 50, 50, 3),
        outputShape = intArrayOf(1, 200, 200, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "IMDN",
        "Remove blurr de imagens",
        "hello.",
        "imdn_rtc_time.tflite",
        inputShape = intArrayOf(1, 720, 480, 3),
        outputShape = intArrayOf(1, 1440, 960, 3),
        inputDataType = DataType.FLOAT32,
        outputDataType = DataType.FLOAT32
    ),
    Model(
        "Yolox tiny",
        "Detecção de objeto",
        "hello.",
        "yolox_tiny_full_integer_quant.opt.tflite",
        inputShape = intArrayOf(1, 416, 416, 3),
        outputShape = intArrayOf(100, 100, 100, 1, 1),
        inputDataType = DataType.UINT8,
        outputDataType = DataType.FLOAT32,
    )
)