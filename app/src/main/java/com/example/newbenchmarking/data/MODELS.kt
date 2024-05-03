package com.example.newbenchmarking.data

import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization
import org.tensorflow.lite.DataType

val MODELS: List<Model> = listOf(
    Model(
        "Efficientnet",
        "Classificação de imagem",
        "O EfficientNet é um modelo de machine learning otimizado para classificação de imagens. Sua arquitetura eficiente e escalável o torna versátil para lidar com uma variedade de desafios em visão computacional.",
        "efficientNetFP32.tflite",
        category = Category.CLASSIFICATION,
        quantization = Quantization.FP32,
    ),
    Model(
        "Efficientnet",
        "Classificação de imagem",
        "hello.",
        "efficientNetINT8.tflite",
        category = Category.CLASSIFICATION,
        quantization = Quantization.INT8,
    ),
    Model(
        "DeepLab v3",
        "Segmentação de imagem",
        "hello.",
        "deeplabv3.tflite",
        category = Category.SEGMENTATION,
        quantization = Quantization.FP32,
    ),
    Model(
        "SSD MobileNet v1",
        "Detecção de objeto",
        "hello.",
        "ssd_mobilenet_v2.tflite",
        category = Category.DETECTION,
        quantization = Quantization.INT8,
    ),
    Model(
        "Yolo v4",
        "Detecção de objeto",
        "hello.",
        "--yolov4-tiny-416-fp32.tflite",
        outputShape = intArrayOf(1, 416, 416, 3),
        category = Category.DETECTION,
        quantization = Quantization.FP32,
    ),
    Model(
        "Yolo v4",
        "Detecção de objeto",
        "hello.",
        "--yolov4-tiny-416-fp16.tflite",
        outputShape = intArrayOf(1, 416, 416, 3),
        category = Category.DETECTION,
        quantization = Quantization.FP16,
    ),
    Model(
        "Yolo v4",
        "Detecção de objeto",
        "hello.",
        "--yolov4-tiny-416-int8.tflite",
        category = Category.DETECTION,
        quantization = Quantization.INT8,
    ),
    Model(
        "ESRGAN",
        "Aumenta resolução de imagens",
        "hello.",
        "esrgan.tflite",
        category = Category.IMAGE_SUPER_RESOLUTION,
        quantization = Quantization.FP32,
    ),
    Model(
        "IMDN",
        "Remove blurr de imagens",
        "hello.",
        "imdn_rtc_time.tflite",
        category = Category.IMAGE_DEBLURRING,
        quantization = Quantization.FP32,
    ),
    Model(
        "Yolox tiny",
        "Detecção de objeto",
        "hello.",
        "yolox_tiny_full_integer_quant.opt.tflite",
        category = Category.DETECTION,
        quantization = Quantization.INT8,
    ),
    Model(
        "Bert",
        "Perguntas e respostas",
        "Perguntas e respostas",
        "bert.tflite",
        category = Category.BERT,
        quantization = Quantization.INT32,
    )
)