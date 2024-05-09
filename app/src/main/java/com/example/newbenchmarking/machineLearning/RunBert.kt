package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.InferenceParams
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions
import java.io.File
import kotlin.system.measureTimeMillis


data class LanguageModelInput(
    val context: String,
    val question: String
)

fun runBert(androidContext: Context, params: InferenceParams, inputs: List<LanguageModelInput>): Inference {

    val baseOptions = BaseOptions.builder()
        .setNumThreads(params.numThreads)

    if(params.useGPU)
        baseOptions.useGpu()
    if(params.useNNAPI)
        baseOptions.useNnapi()

    val options = BertQuestionAnswererOptions.builder()
        .setBaseOptions(baseOptions.build())
        .build()

    val answerer: BertQuestionAnswerer

    val loadTime = measureTimeMillis {
        answerer = BertQuestionAnswerer.createFromFileAndOptions(
            androidContext, File(androidContext.filesDir, params.model.filename).path, options
        )
    }

    val numTests = params.numImages
    var totalTime = 0L
    var firstInferenceTime: Long? = null

    for(i in 0..<numTests){
        val inferenceTime = measureTimeMillis {
            answerer.answer(inputs[i].context, inputs[i].question)
        }

        if(i != 1){
            totalTime += inferenceTime
        }else{
            firstInferenceTime = inferenceTime
        }
    }

    return Inference(
        load = loadTime.toInt(),
        average = (totalTime/(numTests-1)).toInt(),
        first = firstInferenceTime?.toInt(),
        standardDeviation = null
    )
}