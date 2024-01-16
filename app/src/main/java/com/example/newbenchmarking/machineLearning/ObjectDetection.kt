package com.example.newbenchmarking.machineLearning

import android.content.Context
import com.example.newbenchmarking.interfaces.InferenceParams
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import kotlin.system.measureTimeMillis

fun objectDetection(context: Context, params: InferenceParams, images: List<TensorImage>, baseOptionsBuilder: BaseOptions.Builder): Pair<Long, Long>  {
    var totalInferenceTime = 0L
    var objectDetector: ObjectDetector

    val options = ObjectDetector.ObjectDetectorOptions.builder()
        .setBaseOptions(baseOptionsBuilder.build())
        .setMaxResults(1)
        .build()

    val loadTime = measureTimeMillis {
        objectDetector = ObjectDetector.createFromFileAndOptions(
            context, params.model.filename, options
        )
    }

    for(tensorImage in images){
        totalInferenceTime += measureTimeMillis {
            objectDetector.detect(tensorImage)
        }
    }

    val mediumInferenceTime = (totalInferenceTime/images.size)

    return Pair(loadTime, mediumInferenceTime)
}