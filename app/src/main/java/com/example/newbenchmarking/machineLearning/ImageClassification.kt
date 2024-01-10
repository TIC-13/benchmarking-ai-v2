package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import kotlin.system.measureTimeMillis

fun runImageClassification(context: Context, modelPath: String, images: List<TensorImage>, useNNAPI: Boolean =  false): Pair<Long, Long> {
    var totalInferenceTime = 0L
    var imageClassifier: ImageClassifier

    var builder = BaseOptions.builder()
    if(useNNAPI) builder = builder.useNnapi()

    val options = ImageClassifier.ImageClassifierOptions.builder()
        .setBaseOptions(builder.build())
        .setMaxResults(1000)
        .build()

    val loadTime = measureTimeMillis {
        imageClassifier = ImageClassifier.createFromFileAndOptions(
            context, modelPath, options
        )
    }

    for(tensorImage in images){
        totalInferenceTime += measureTimeMillis {
            imageClassifier.classify(tensorImage)
        }
    }

    val mediumInferenceTime = (totalInferenceTime/images.size)

    return Pair(loadTime, mediumInferenceTime)
}