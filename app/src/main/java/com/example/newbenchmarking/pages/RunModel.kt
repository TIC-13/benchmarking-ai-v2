package com.example.newbenchmarking.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.machineLearning.runTfLiteModel
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.InferenceViewModel
import com.example.newbenchmarking.viewModel.ResultViewModel
import getBitmapImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun RunModel(modifier: Modifier = Modifier, viewModel: InferenceViewModel, resultViewModel: ResultViewModel, goToResults: () -> Unit) {

    val inferencesList by viewModel.inferenceParamsList.observeAsState(
        initial = arrayListOf(InferenceParams(
            model = models[0],
            numImages = 50,
            numThreads = 1,
            useGPU = false,
            useNNAPI = false
        ))
    )

    val resultsList by resultViewModel.inferenceResultList.observeAsState()

    val context = LocalContext.current

    var cpuUsage by remember { mutableStateOf(CpuUsage()) }
    var displayCpuUsage by remember {mutableStateOf("0%")}

    var ramUsage by remember { mutableStateOf(RamUsage()) }
    var displayRamUsage by remember { mutableStateOf("0MB") }

    var gpuUsage by remember { mutableStateOf(GpuUsage())}
    var displayGpuUsage by remember { mutableStateOf("0%") }

    var currParams by remember { mutableStateOf(inferencesList[0]) }

    LaunchedEffect(Unit){
        for(inferenceParams in inferencesList){

            currParams = inferenceParams
            var result: Pair<Long, Long>

            val currImages = getBitmapImages(context, inferenceParams.numImages)

            withContext(Dispatchers.IO){
                result = runTfLiteModel(context, inferenceParams, currImages)
            }

            val currResult = InferenceResult(
                loadTime = result.first,
                inferenceTimeAverage = result.second,
                ramConsumedAverage = ramUsage.getAverage(),
                gpuAverage = gpuUsage.getAverage(),
                cpuAverage = cpuUsage.getAverageCPUConsumption(),
                params = inferenceParams,
            )

            resultViewModel.updateInferenceResultList(
                ArrayList(
                    resultsList?.plus(arrayListOf(currResult))
                        ?: arrayListOf(currResult)
                )
            )

            gpuUsage = GpuUsage()
            cpuUsage = CpuUsage()
            ramUsage = RamUsage()
        }

        goToResults()
    }

    LaunchedEffect(Unit) {
        while(true){
            delay(100)
            cpuUsage.calculateCPUUsage()
            ramUsage.calculateUsage()
            gpuUsage.calculateUsage()
        }
    }

    LaunchedEffect(Unit) {
        while(true){
            delay(500)
            displayCpuUsage = "%.2f".format(cpuUsage.getCPUUsage()) + "%"
            displayRamUsage = ramUsage.get().toString() + "MB"
            displayGpuUsage = gpuUsage.get().toString() + "%"
        }
    }

    BackgroundWithContent (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(0.dp, 200.dp),
                color = LocalAppColors.current.primary,
                trackColor = LocalAppColors.current.secondary
            )
        }
        InferenceView(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)),
            params = currParams,
            cpuUsage = displayCpuUsage,
            gpuUsage = displayGpuUsage,
            ramUsage = displayRamUsage
        )
    }
}

