package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.viewModel.ResultViewModel
import androidx.compose.foundation.lazy.items
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.ScoreView
import com.example.newbenchmarking.components.TitleView
import com.example.newbenchmarking.theme.LocalAppColors

@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.inferenceResultList.observeAsState(
        initial = arrayListOf(InferenceResult(
            cpuAverage = 0F,
            gpuAverage =  0,
            ramConsumedAverage =  0F,
            inferenceTimeAverage = 0L,
            loadTime = 0L,
            params = InferenceParams(
                model = models[0],
                numImages = 50,
                numThreads = 1,
                useGPU = false,
                useNNAPI = false
            )
        ))
    )

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
                    infTime = result.inferenceTimeAverage.toString() + "ms"
                )
            }
        }
    }
}
