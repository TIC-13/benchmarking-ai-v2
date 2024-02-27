package com.example.newbenchmarking.pages

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.viewModel.ResultViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.ScoreView
import com.example.newbenchmarking.data.DEFAULT_PARAMS
import java.io.File

@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.inferenceResultList.observeAsState(
        initial = arrayListOf(
            DEFAULT_PARAMS
        )
    )

    val context = LocalContext.current

    LaunchedEffect(Unit) {

        data class BenchPair(
            val label: String,
            val content: String,
        )

        val csvList = mutableListOf<List<String>>()

        for((index, result) in resultList.withIndex()){

            val line = arrayOf(
                BenchPair("manufacturer", Build.MANUFACTURER),
                BenchPair("phone_model", Build.MODEL),
                BenchPair("ml_model", result.params.model.label),
                BenchPair("category", result.params.model.category.toString()),
                BenchPair("quantization", result.params.model.quantization.toString()),
                BenchPair("dataset", result.params.dataset.label),
                BenchPair("num_images", result.params.numImages.toString()),
                BenchPair("usesNNAPI", result.params.useNNAPI.toString()),
                BenchPair("usesGPU", result.params.useGPU.toString()),
                BenchPair("num_threads", result.params.numThreads.toString()),
                BenchPair("cpu_usage", result.cpuAverage.toString()),
                BenchPair("gpu_usage", result.gpuAverage.toString()),
                BenchPair("ram_usage", result.ramConsumedAverage.toString()),
                BenchPair("load_time", result.loadTime.toString()),
                BenchPair("inference_time", result.inferenceTimeAverage.toString()),
                BenchPair("timestamp", System.currentTimeMillis().toString())
            )

            if(index == 0){
                //csvList.add(line.map{it.label})
                println(line.map{it.label}.joinToString(","))
            }
            //csvList.add(line.map{it.content})
            println(line.map{it.content}.joinToString(","))
        }

        //createAndSaveCSV(context, "${Build.MANUFACTURER} ${Build.MODEL}", csvList)
    }

    fun onBack(){
        resultViewModel.updateInferenceResultList(arrayListOf())
        back()
    }

    BackgroundWithContent(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {

        ScoreView()

        Button(onClick = { onBack() }) {
            Text(text = "Continuar", color = Color.White)
        }

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            items(resultList) { result ->
                InferenceView(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(50.dp)),
                    params = result.params,
                    cpuUsage = String.format("%.2f", result.cpuAverage) + "%",
                    gpuUsage = result.gpuAverage.toString() + "%",
                    ramUsage = result.ramConsumedAverage.toInt().toString() + "MB",
                    initTime = result.loadTime.toString() + "ms",
                    infTime = result.inferenceTimeAverage.toString() + "ms",
                    showInfoButton = true
                )
            }
        }
    }
}

fun createAndSaveCSV(context: Context, fileName: String, data: List<List<String>>) {
    val stringBuilder = StringBuilder()
    data.forEach { row ->
        stringBuilder.append(row.joinToString(",")).append("\n")
    }

    context.filesDir?.let { filesDir ->
        val file = File(filesDir, fileName)
        file.writeText(stringBuilder.toString())
    }
}
