package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.RunMode
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun runTfLiteModel(
    context: Context,
    params: InferenceParams,
    images: List<Bitmap>,
    setImagesIndex: (newIndex: Int) -> Unit
): Inference {

    val modelBuffer = if (params.model.file != null) {
        loadModelFileFromExternal(params.model.file!!)
    } else if (params.model.filename != null) {
        loadModelFileFromAssets(context, params.model.filename!!)
    } else {
        throw IllegalArgumentException("Model must have either a file or a filename.")
    }

    val gpuDelegate = GpuDelegate()

    val options = Interpreter.Options().apply {
        if (params.runMode == RunMode.GPU) {
            this.addDelegate(gpuDelegate)
        }
        useNNAPI = params.runMode == RunMode.NNAPI
        numThreads = params.numThreads
    }

    val interpreter: Interpreter

    val loadTime = measureTimeMillis {
        interpreter = Interpreter(modelBuffer, options)
    }

    var totalInferenceTime = 0L
    var firstInferenceTime: Long? = null

    var inferencesList = mutableListOf<Long>()

    val inputTensor = interpreter.getInputTensor(0)
    val outputTensor = interpreter.getOutputTensor(0)
    val inputShape = params.model.inputShape ?: inputTensor.shape()
    val outputShape = params.model.outputShape ?: outputTensor.shape()
    val inputType = params.model.inputDataType ?: inputTensor.dataType()
    val outputType = params.model.outputDataType ?: outputTensor.dataType()

    val imageProcessorBuilder = ImageProcessor.Builder()
    imageProcessorBuilder
        .add(ResizeOp(inputShape[2], inputShape[1], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
    imageProcessorBuilder.add(CastOp(inputType))

    setImagesIndex(0)

    images.forEachIndexed { index, bitmap ->

        setImagesIndex(index + 1)

        val tensorImage = TensorImage(inputType)
        tensorImage.load(bitmap)

        val input = imageProcessorBuilder.build().process(tensorImage)
        val output = TensorBuffer.createFixedSize(outputShape, outputType)

        val inferenceTime = measureTimeMillis {
            interpreter.run(input.buffer, output.buffer)
        }

        if (index != 0) {
            totalInferenceTime += inferenceTime
            inferencesList.add(inferenceTime)
        } else {
            firstInferenceTime = inferenceTime
        }
    }

    interpreter.close()
    gpuDelegate.close()
    val mediumInferenceTime = (totalInferenceTime / images.size - 1)

    val standardDeviation = sqrt(inferencesList.sumOf {
        (it - mediumInferenceTime).toDouble().pow(2.0)
    } / inferencesList.size)

    if (firstInferenceTime !== null && mediumInferenceTime != 0L) {
        val runMode = if (params.runMode == RunMode.NNAPI) "NNAPI" else if (params.runMode == RunMode.GPU) "GPU" else "CPU"
        val tag = "${params.model.label} ${params.model.quantization} ${runMode}"

        Log.d("inftime", "$tag - ${params.numImages} imagens")
        Log.d("inftime", "Primeira inferência: $firstInferenceTime")
        Log.d("inftime", "Média das outras: $mediumInferenceTime")
        Log.d("inftime", "Desvio padrão: $standardDeviation")
        Log.d("inftime", "")
    }

    return Inference(
        load = loadTime.toInt(),
        average = mediumInferenceTime.toInt(),
        first = firstInferenceTime?.toInt(),
        standardDeviation = standardDeviation.toInt()
    )
}

fun loadModelFileFromExternal(file: File): ByteBuffer {
    val fileInputStream = FileInputStream(file)
    val fileChannel = fileInputStream.channel
    val startOffset = 0L
    val declaredLength = fileChannel.size()
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

fun loadModelFileFromAssets(context: Context, filename: String): ByteBuffer {
    context.assets.openFd(filename).use { assetFileDescriptor ->
        FileInputStream(assetFileDescriptor.fileDescriptor).channel.use { fileChannel ->
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }
}
