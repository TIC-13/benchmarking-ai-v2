package com.example.newbenchmarking.pages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.machineLearning.LanguageModelInput
import com.example.newbenchmarking.machineLearning.runBert
import com.example.newbenchmarking.machineLearning.runTfLiteModel
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.InferenceViewModel
import com.example.newbenchmarking.viewModel.ResultViewModel
import getBitmapImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun RunModel(modifier: Modifier = Modifier, viewModel: InferenceViewModel, resultViewModel: ResultViewModel, goToResults: () -> Unit) {

    val inferencesList by viewModel.inferenceParamsList.observeAsState()
    val resultsList by resultViewModel.benchmarkResultList.observeAsState()

    val context = LocalContext.current

    if(inferencesList === null || inferencesList!!.isEmpty()) return
    val paramsList = inferencesList!!

    var cpuUsage by remember { mutableStateOf(CpuUsage()) }
    var displayCpuUsage by remember {mutableIntStateOf(0)}

    var ramUsage by remember { mutableStateOf(RamUsage()) }
    var displayRamUsage by remember { mutableIntStateOf(0) }

    var gpuUsage by remember { mutableStateOf(GpuUsage())}
    var displayGpuUsage by remember { mutableIntStateOf(0) }

    var currParams by remember { mutableStateOf(paramsList[0]) }

    LaunchedEffect(Unit){
        for(inferenceParams in paramsList){

            var result = Inference()
            var errorMessage: String? = null
            var images: List<Bitmap>? = null

            withContext(Dispatchers.IO){

                currParams = inferenceParams

                try {
                    result = if(inferenceParams.model.category === Category.BERT) {
                        val parsedInput = parseLanguageInput(context, inferenceParams.dataset.path)
                        runBert(context, inferenceParams, parsedInput)
                    }else{
                        images = getBitmapsFromAssetsFolder(
                            context,
                            folderName = inferenceParams.dataset.path,
                            numBitmaps = inferenceParams.numImages
                        )
                        runTfLiteModel(context, inferenceParams, images!!)
                    }

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

            withContext(Dispatchers.IO){
                if (images != null) {
                    for(image in images!!)
                        image.recycle()
                }
            }

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
            bottomSecondTitle = currParams.dataset.name,
            rows = arrayOf(
                ResultRow("Uso de CPU", "$displayCpuUsage%"),
                ResultRow("Uso de GPU", "$displayGpuUsage%"),
                ResultRow("Uso de RAM", "${displayRamUsage}MB")
            )
        )
    }
}

fun getBitmapsFromAssetsFolder(context: Context, folderName: String, numBitmaps: Int): List<Bitmap> {
    val filenames = getFilesFromAssetFolder(context, folderName).subList( 0, numBitmaps )
    return filenames.mapNotNull { filename ->
        loadBitmapFromAssets(context, folderName, filename)
    }
}

fun loadBitmapFromAssets(context: Context, folderName: String, filename: String): Bitmap? {
    return try {
        context.assets.open("$folderName/$filename").use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun getFilesFromAssetFolder(context: Context, folderName: String): List<String> {
    return try {
        context.assets.list(folderName)?.toList() ?: emptyList()
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}

fun parseLanguageInput(context: Context, filePath: String): List<LanguageModelInput> {
    return try {
        val fileContent = context.assets.open(filePath).bufferedReader().use { it.readText() }
        val contextQuestionPair = fileContent.split("\r\n\r\n")
        contextQuestionPair.map {
            val separatedPair = it.split("\r\n")
            LanguageModelInput(
                context = separatedPair[0],
                question = separatedPair[1]
            )
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error loading file: ${e.message}"
        throw e
    }
}



