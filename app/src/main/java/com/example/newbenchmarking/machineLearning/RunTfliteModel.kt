package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.example.newbenchmarking.interfaces.InferenceParams
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

data class RunModelResult(
    val load: Long? = null,
    val average: Long? = null,
    val first: Long? = null,
    val standardDeviation: Double? = null
)

fun runTfLiteModel(context: Context, params: InferenceParams, images: List<Bitmap>): RunModelResult {

    val model = loadModelFile(context.assets, modelName = params.model.filename)
    val gpuDelegate = GpuDelegate()

    val options = Interpreter.Options().apply {
        if(params.useGPU){
            this.addDelegate(gpuDelegate)
        }
        useNNAPI = params.useNNAPI
        numThreads = params.numThreads
    }

    val interpreter: Interpreter

    val loadTime = measureTimeMillis {
        interpreter = Interpreter(model, options)
    }

    var totalInferenceTime = 0L
    var firstInferenceTime: Long? = null

    var inferencesList = mutableListOf<Long>()

    images.forEachIndexed{ index, bitmap ->
        val imageProcessorBuilder = ImageProcessor.Builder()

        imageProcessorBuilder
            .add(ResizeOp(params.model.inputShape[2], params.model.inputShape[1], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))

        imageProcessorBuilder.add(CastOp(params.model.inputDataType))
        val tensorImage = TensorImage(params.model.inputDataType)
        tensorImage.load(bitmap)

        val input = imageProcessorBuilder.build().process(tensorImage)
        val output = TensorBuffer.createFixedSize(params.model.outputShape, params.model.outputDataType)

        val inferenceTime = measureTimeMillis {
            interpreter.run(input.buffer, output.buffer)
        }

        if(index != 0){
            totalInferenceTime += inferenceTime
            inferencesList.add(inferenceTime)
        }else{
            firstInferenceTime = inferenceTime
        }

        bitmap.recycle()
    }

    interpreter.close()
    gpuDelegate.close()
    val mediumInferenceTime = (totalInferenceTime/images.size - 1)

    val standardDeviation = sqrt(inferencesList.sumOf {
        (it - mediumInferenceTime).toDouble().pow(2.0)
    } / inferencesList.size)

    if(firstInferenceTime !== null && mediumInferenceTime != 0L){
        val runMode = if(params.useNNAPI) "NNAPI" else if(params.useGPU) "GPU" else "CPU"
        val tag = "${params.model.label} ${params.model.quantization} ${runMode}"

        Log.d("inftime", "$tag - ${params.numImages} imagens")
        Log.d("inftime", "Primeira inferência: $firstInferenceTime")
        Log.d("inftime", "Média das outras: $mediumInferenceTime")
        Log.d("inftime", "Desvio padrão: $standardDeviation")
        Log.d("inftime", "")
    }

    return RunModelResult(
        load = loadTime,
        average = mediumInferenceTime,
        first = firstInferenceTime,
        standardDeviation = standardDeviation
    )
}

fun loadModelFile(assetManager: AssetManager, modelName: String): MappedByteBuffer {
    val fileDescriptor = assetManager.openFd(modelName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}