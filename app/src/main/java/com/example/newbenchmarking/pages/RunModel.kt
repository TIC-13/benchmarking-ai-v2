package com.example.newbenchmarking.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.CPUChip
import com.example.newbenchmarking.components.GPUChip
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.NNAPIChip
import com.example.newbenchmarking.components.ResultRow
import com.example.newbenchmarking.data.DEFAULT_PARAMS
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.machineLearning.runBert
import com.example.newbenchmarking.machineLearning.runTfLiteModel
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
        initial = arrayListOf(
            DEFAULT_PARAMS.params
        )
    )

    val resultsList by resultViewModel.benchmarkResultList.observeAsState()

    val context = LocalContext.current

    var cpuUsage by remember { mutableStateOf(CpuUsage()) }
    var displayCpuUsage by remember {mutableIntStateOf(0)}

    var ramUsage by remember { mutableStateOf(RamUsage()) }
    var displayRamUsage by remember { mutableIntStateOf(0) }

    var gpuUsage by remember { mutableStateOf(GpuUsage())}
    var displayGpuUsage by remember { mutableIntStateOf(0) }

    var currParams by remember { mutableStateOf(inferencesList[0]) }

    LaunchedEffect(Unit){
        for(inferenceParams in inferencesList){

            currParams = inferenceParams
            var result = Inference()
            var errorMessage: String? = null

            val currImages = getBitmapImages(context, inferenceParams.dataset.imagesId, inferenceParams.numImages)

            withContext(Dispatchers.IO){
                try {
                    result = if(inferenceParams.model.category === Category.BERT)
                        runBert(context, inferenceParams)
                    else
                        runTfLiteModel(context, inferenceParams, currImages)
                }catch (e: Exception){
                        errorMessage = e.toString()
                }
            }

            val isError = errorMessage !== null
            val currResult = BenchmarkResult(
                inference = result,
                params = inferenceParams,
                ram = ramUsage,
                cpu = cpuUsage,
                gpu = gpuUsage,
                isError = isError,
                errorMessage = errorMessage,
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
            withContext(Dispatchers.IO) {
                delay(5)
                cpuUsage.calculateCPUUsage()
                ramUsage.calculateUsage()
                gpuUsage.calculateUsage()
            }
        }
    }

    LaunchedEffect(Unit) {
        while(true){
            delay(500)
            displayCpuUsage = cpuUsage.getCPUUsage().toInt()
            displayRamUsage = ramUsage.get()
            displayGpuUsage = gpuUsage.get()
        }
    }

    BackHandler(enabled = true){}

    BackgroundWithContent (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight(0.5F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Carregando...", style = LocalAppTypography.current.title)
        }
        InferenceView(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)),
            topTitle = "${currParams.model.label} - ${currParams.model.quantization}",
            subtitle = currParams.model.description,
            chip = if(currParams.useNNAPI) NNAPIChip() else if (currParams.useGPU) GPUChip() else CPUChip(),
            bottomFirstTitle = "${currParams.numImages} ${if(currParams.model.category !== Category.BERT) "imagens" else "inferências"} - ${currParams.numThreads} thread${if(currParams.numThreads != 1) "s" else ""}",
            bottomSecondTitle = currParams.dataset.label,
            rows = arrayOf(
                ResultRow("Uso de CPU", "$displayCpuUsage%"),
                ResultRow("Uso de GPU", "$displayGpuUsage%"),
                ResultRow("Uso de RAM", "${displayRamUsage}MB")
            )
        )
    }
}

