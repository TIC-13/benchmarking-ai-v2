package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
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
import kotlin.system.measureTimeMillis

fun runTfLiteModel(context: Context, params: InferenceParams, images: List<Bitmap>): Pair<Long, Long> {

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

    for(bitmap in images){

        totalInferenceTime += measureTimeMillis {

            val imageProcessorBuilder = ImageProcessor.Builder()

            imageProcessorBuilder
                .add(ResizeOp(params.model.inputShape[2], params.model.inputShape[1], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))

            imageProcessorBuilder.add(CastOp(params.model.inputDataType))
            val tensorImage = TensorImage(params.model.inputDataType)
            tensorImage.load(bitmap)

            val input = imageProcessorBuilder.build().process(tensorImage)
            val output = TensorBuffer.createFixedSize(params.model.outputShape, params.model.outputDataType)

            interpreter.run(input.buffer, output.buffer)

        }
    }

    val mediumInferenceTime = (totalInferenceTime/images.size)
    return Pair(loadTime, mediumInferenceTime)
}

fun loadModelFile(assetManager: AssetManager, modelName: String): MappedByteBuffer {
    val fileDescriptor = assetManager.openFd(modelName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}