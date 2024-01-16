package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.machineLearning.runTfliteModel
import com.example.newbenchmarking.utils.getImage
import com.example.newbenchmarking.utils.getImagesIdList
import com.example.newbenchmarking.viewModel.InferenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage

@Composable
fun RunModel(modifier: Modifier = Modifier, viewModel: InferenceViewModel, goToResults: (InferenceResult) -> Unit) {

    val params by viewModel.inferenceParams.observeAsState(
        initial = InferenceParams(
            model = models[0],
            numImages = 50,
            numThreads = 1,
            useGPU = false,
            useNNAPI = false
        )
    )

    val context = LocalContext.current
    val loadingLable = "Carregando..."

    var imagesIdList = getImagesIdList(params.numImages)
    var tensorImages = imagesIdList.map { TensorImage.fromBitmap(getImage(id = it)) }

    val cpuUsage by remember { mutableStateOf(CpuUsage()) }
    var displayCpuUsage by remember {mutableStateOf("0%")}

    val ramUsage by remember { mutableStateOf(RamUsage()) }
    var displayRamUsage by remember { mutableStateOf("0MB") }

    val gpuUsage by remember { mutableStateOf(GpuUsage())}
    var displayGpuUsage by remember { mutableStateOf("0%") }

    LaunchedEffect(Unit){
        var result: Pair<Long, Long>
        withContext(Dispatchers.IO){
            result = runTfliteModel(context, params, tensorImages)
        }

        imagesIdList = emptyList()
        tensorImages = emptyList()

        goToResults(InferenceResult(
            loadTime = result.first,
            inferenceTimeAverage = result.second,
            ramConsumedAverage = ramUsage.getAverage(),
            gpuAverage = gpuUsage.getAverage(),
            cpuAverage = cpuUsage.getAverageCPUConsumption()
        ))
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

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = loadingLable,
            modifier = modifier
        )
        Text(
            text = params.model.label,
            modifier = modifier
        )
        Text(
            text = params.numImages.toString() + " imagens",
            modifier = modifier
        )
        Text(
            text = params.numThreads.toString() + " threads",
            modifier = modifier
        )
        if(params.useNNAPI) {
            Text(
                text = "NNAPI ativa",
                modifier = modifier
            )
        }
        if(params.useGPU) {
            Text(
                text = "Delegado de GPU ativo",
                modifier = modifier
            )
        }
        Text(
            text = "CPU: $displayCpuUsage",
            modifier = modifier
        )
        Text(
            text = "GPU: $displayGpuUsage",
            modifier = modifier
        )
        Text(
            text = "RAM: $displayRamUsage",
            modifier = modifier
        )
    }
}