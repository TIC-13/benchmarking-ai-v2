package ai.luxai.speedai.machineLearning

import android.content.Context
import android.util.Log
import ai.luxai.speedai.interfaces.Inference
import ai.luxai.speedai.interfaces.InferenceParams
import ai.luxai.speedai.interfaces.RunMode
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions
import org.tensorflow.lite.task.text.qa.QaAnswer
import java.io.File
import kotlin.system.measureTimeMillis


data class LanguageModelInput(
    val context: String,
    val question: String
)

fun runBert(androidContext: Context, params: InferenceParams, inputs: List<LanguageModelInput>, file: File): Inference {

    val baseOptions = BaseOptions.builder()
        .setNumThreads(params.numThreads)

    if(params.runMode == RunMode.GPU)
        baseOptions.useGpu()
    if(params.runMode == RunMode.NNAPI)
        baseOptions.useNnapi()

    val options = BertQuestionAnswererOptions.builder()
        .setBaseOptions(baseOptions.build())
        .build()

    val answerer: BertQuestionAnswerer

    val loadTime = measureTimeMillis {
        answerer = BertQuestionAnswerer.createFromFileAndOptions(file, options)
    }

    val numTests = params.numImages
    var totalTime = 0L
    var firstInferenceTime: Long? = null
    var totalChars = 0

    for(i in 0..<numTests){

        var ans: MutableList<QaAnswer>? = null

        val inferenceTime = measureTimeMillis {
            ans = answerer.answer(inputs[i].context, inputs[i].question)
        }

        if(ans !== null && ans!!.isNotEmpty()){
            totalChars += ans!![0].text.length
        }

        if(i != 0){
            totalTime += inferenceTime
        }else{
            firstInferenceTime = inferenceTime
        }
    }

    return Inference(
        load = loadTime.toInt(),
        average = (totalTime/(numTests-1)).toInt(),
        first = firstInferenceTime?.toInt(),
        standardDeviation = null,
        charsPerSecond = (totalChars/(totalTime/1000)).toInt()
    )
}