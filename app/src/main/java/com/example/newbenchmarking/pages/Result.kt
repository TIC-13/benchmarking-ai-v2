package com.example.newbenchmarking.pages

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
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.ScoreView
import com.example.newbenchmarking.data.DEFAULT_PARAMS

@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.inferenceResultList.observeAsState(
        initial = arrayListOf(
            DEFAULT_PARAMS
        )
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
