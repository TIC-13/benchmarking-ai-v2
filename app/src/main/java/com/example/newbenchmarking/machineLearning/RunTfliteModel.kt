package com.example.newbenchmarking.machineLearning

import android.content.Context
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.ModelType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions

fun runTfliteModel(context: Context, params: InferenceParams, images: List<TensorImage>): Pair<Long, Long> {
    var builder = BaseOptions.builder()
    if(params.useNNAPI) builder = builder.useNnapi()

    return if(params.model.modelType == ModelType.SEGMENTATION){
        imageSegmentation(context, params, images, builder)
    }else{
        imageClassification(context, params, images, builder)
    }
}