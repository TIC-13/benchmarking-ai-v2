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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight(0.66F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                color = LocalAppColors.current.primary,
                trackColor = LocalAppColors.current.secondary
            )
        }
        InferenceView(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            params = currParams,
            cpuUsage = displayCpuUsage,
            gpuUsage = displayGpuUsage,
            ramUsage = displayRamUsage
        )
    }
}

@Composable
fun InferenceView(
    modifier: Modifier = Modifier,
    params: InferenceParams,
    cpuUsage: String,
    gpuUsage: String,
    ramUsage: String
) {

    val rows = arrayOf(
        TableRow("Uso de CPU", cpuUsage),
        TableRow("Uso de GPU", gpuUsage),
        TableRow("Uso de RAM", ramUsage)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
            .background(LocalAppColors.current.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.4F),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = params.model.label,
                        style = LocalAppTypography.current.tableTitle
                    )
                    Text(
                        text = params.model.description,
                        style = LocalAppTypography.current.tableSubtitle
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(params.useNNAPI)
                                Chip(text = "NNAPI")
                            if(params.useGPU)
                                Chip(text = "GPU")
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalAppColors.current.secondary)
                    .padding(0.dp, 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 20.dp),
                    text = "${params.numImages} Imagens - ${params.numThreads} thread" +
                            if(params.numThreads == 1) "" else "s",
                    style = LocalAppTypography.current.tableIndex
                )
                for(row in rows){
                    TextRow(row)
                }
            }
        }
    }
}

data class TableRow(
    val index: String,
    val value: String
)
@Composable
fun TextRow(row: TableRow) {
    Row (
        modifier = Modifier
            .fillMaxWidth(0.7F)
            .padding(0.dp, 0.dp, 0.dp, 10.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7F)
        ){
            Text(
                text = row.index,
                style = LocalAppTypography.current.tableIndex
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = row.value,
                style = LocalAppTypography.current.tableContent
            )
        }
    }
}


@Composable
fun Chip(text: String){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(LocalAppColors.current.secondary)
            .padding(25.dp, 5.dp),
    ){
        Text(
            text = text,
            style = LocalAppTypography.current.chip
        )
    }

}