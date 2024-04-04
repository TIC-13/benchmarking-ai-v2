package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.InferenceParams
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions
import kotlin.system.measureTimeMillis

fun runBert(androidContext: Context, params: InferenceParams): Pair<Long, Long> {

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
            androidContext, params.model.filename, options
        )
    }

    val contextOfTheQuestion = "Google LLC is an American multinational technology company that specializes in Internet-related services and products, which include online advertising technologies, search engine, cloud computing, software, and hardware. It is considered one of the Big Four technology companies, alongside Amazon, Apple, and Facebook.\n" +
            "\n" +
            "Google was founded in September 1998 by Larry Page and Sergey Brin while they were Ph.D. students at Stanford University in California. Together they own about 14 percent of its shares and control 56 percent of the stockholder voting power through supervoting stock. They incorporated Google as a California privately held company on September 4, 1998, in California. Google was then reincorporated in Delaware on October 22, 2002. An initial public offering (IPO) took place on August 19, 2004, and Google moved to its headquarters in Mountain View, California, nicknamed the Googleplex. In August 2015, Google announced plans to reorganize its various interests as a conglomerate called Alphabet Inc. Google is Alphabet's leading subsidiary and will continue to be the umbrella company for Alphabet's Internet interests. Sundar Pichai was appointed CEO of Google, replacing Larry Page who became the CEO of Alphabet."
    val questionToAsk = "Who is the CEO of Google?"

    val inferenceTime = measureTimeMillis {
        val answers = answerer.answer(contextOfTheQuestion, questionToAsk)
        Log.d("Answers", answers[0].text)
    }

    return Pair(loadTime, inferenceTime)
}