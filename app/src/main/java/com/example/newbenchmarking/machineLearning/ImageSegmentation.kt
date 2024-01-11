package com.example.newbenchmarking.machineLearning

import android.content.Context
import com.example.newbenchmarking.interfaces.InferenceParams
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.segmenter.ImageSegmenter
import org.tensorflow.lite.task.vision.segmenter.OutputType
import kotlin.system.measureTimeMillis

fun imageSegmentation(context: Context, params: InferenceParams, images: List<TensorImage>, baseOptionsBuilder: BaseOptions.Builder): Pair<Long, Long>  {
    var totalInferenceTime = 0L
    var imageSegmenter: ImageSegmenter

    val options = ImageSegmenter.ImageSegmenterOptions.builder()
        .setBaseOptions(baseOptionsBuilder.build())
        .setOutputType(OutputType.CONFIDENCE_MASK)
        .build()

    val loadTime = measureTimeMillis {
        imageSegmenter = ImageSegmenter.createFromFileAndOptions(
            context, params.model.filename, options
        )
    }

    for(tensorImage in images){
        totalInferenceTime += measureTimeMillis {
            imageSegmenter.segment(tensorImage)
        }
    }

    val mediumInferenceTime = (totalInferenceTime/images.size)

    return Pair(loadTime, mediumInferenceTime)
}