package com.example.newbenchmarking.pages

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.benchmark.CpuUsage
import com.example.newbenchmarking.benchmark.Energy
import com.example.newbenchmarking.benchmark.GpuUsage
import com.example.newbenchmarking.benchmark.RamUsage
import com.example.newbenchmarking.benchmark.Samples
import com.example.newbenchmarking.benchmark.getEnergyConsumption
import com.example.newbenchmarking.benchmark.standardDeviation
import com.example.newbenchmarking.benchmark.timeFromEnergyConsumption
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.CPUChip
import com.example.newbenchmarking.components.GPUChip
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.LoadingScreen
import com.example.newbenchmarking.components.NNAPIChip
import com.example.newbenchmarking.components.ResultRow
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.machineLearning.runBert
import com.example.newbenchmarking.machineLearning.runTfLiteModel
import com.example.newbenchmarking.utils.getBitmapsFromFolder
import com.example.newbenchmarking.utils.parseLanguageDataset
import com.example.newbenchmarking.viewModel.InferenceViewModel
import com.example.newbenchmarking.viewModel.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Method

@Composable
fun RunModel(viewModel: InferenceViewModel, resultViewModel: ResultViewModel, goToResults: () -> Unit) {

    val context = LocalContext.current

    val voltages by remember { mutableStateOf(Samples<Float>()) }
    val currents by remember { mutableStateOf(Samples<Float>()) }

    var idleMeasuredEnergy by remember { mutableStateOf<Energy?>(null) }

    LaunchedEffect(Unit) {
        val initialTime = System.currentTimeMillis()
        while(true) {
            delay(10)
            voltages.addSample(getBatteryVoltageVolts(context))
            currents.addSample(getBatteryCurrentAmperes(context))
            if(System.currentTimeMillis() - initialTime > 20000){
                idleMeasuredEnergy = getEnergyConsumption(voltages, currents)
                break
            }
        }
    }

    if(idleMeasuredEnergy === null)
        return LoadingScreen()

    return RunModelScreen(
        viewModel = viewModel,
        resultViewModel = resultViewModel,
        idleMeasuredEnergy = idleMeasuredEnergy!!,
        goToResults = goToResults
    )
}

@Composable
fun RunModelScreen(
    viewModel: InferenceViewModel,
    resultViewModel: ResultViewModel,
    idleMeasuredEnergy: Energy,
    goToResults: () -> Unit
) {

    val context = LocalContext.current

    val inferencesList by viewModel.inferenceParamsList.observeAsState()
    val folder by viewModel.folder.observeAsState()
    val afterRun by viewModel.afterRun.observeAsState()

    if(folder == null)
        throw Error(stringResource(R.string.folder_not_found_error))

    val resultsList by resultViewModel.benchmarkResultList.observeAsState()

    if(inferencesList === null || inferencesList!!.isEmpty()) return
    val paramsList = inferencesList!!

    var cpuUsage by remember { mutableStateOf(CpuUsage()) }
    var displayCpuUsage by remember {mutableIntStateOf(0)}

    var ramUsage by remember { mutableStateOf(RamUsage()) }
    var displayRamUsage by remember { mutableIntStateOf(0) }

    var gpuUsage by remember { mutableStateOf(GpuUsage())}
    var displayGpuUsage by remember { mutableIntStateOf(0) }

    var voltages by remember { mutableStateOf(Samples<Float>()) }
    var currents by remember { mutableStateOf(Samples<Float>()) }
    var sampleVoltagesAndCurrents by remember { mutableStateOf(true) }

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
                        val parsedInput = parseLanguageDataset(File(folder, inferenceParams.dataset.path))
                        runBert(context, inferenceParams, parsedInput, File(folder, inferenceParams.model.filename))
                    }else{
                        images = getBitmapsFromFolder(
                            File(folder, inferenceParams.dataset.path),
                            numBitmaps = inferenceParams.numImages
                        )
                        runTfLiteModel(context, inferenceParams, images!!, File(folder, inferenceParams.model.filename))
                    }

                }catch (e: Exception){
                        errorMessage = e.toString()
                }
            }

            val isError = errorMessage !== null

            sampleVoltagesAndCurrents = false
            val energyConsumption = getEnergyConsumption(voltages, currents)

            val currResult = BenchmarkResult(
                inference = result,
                params = inferenceParams,
                ram = ramUsage,
                cpu = cpuUsage,
                gpu = gpuUsage,
                isError = isError,
                energy = Energy(
                    power = energyConsumption.power - idleMeasuredEnergy.power,
                    joule = energyConsumption.joule - ((idleMeasuredEnergy.power) * timeFromEnergyConsumption(energyConsumption))
                ),
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
            voltages = Samples()
            currents = Samples()

            sampleVoltagesAndCurrents = true

            withContext(Dispatchers.IO){
                if (images != null) {
                    for(image in images!!)
                        image.recycle()
                }
            }

        }
        afterRun?.let { it() }
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
            if(!sampleVoltagesAndCurrents)
                continue
            withContext(Dispatchers.IO) {
                delay(10)
                voltages.addSample(getBatteryVoltageVolts(context))
                currents.addSample(getBatteryCurrentAmperes(context))
            }
        }
    }

    LaunchedEffect(Unit) {
        while(true){
            delay(500)
            displayCpuUsage = cpuUsage.getCPUUsage()
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
            Text(
                text = "Carregando...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        InferenceView(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)),
            topTitle = "${currParams.model.label} - ${currParams.model.quantization}",
            subtitle = currParams.model.description,
            chip = if(currParams.useNNAPI) NNAPIChip() else if (currParams.useGPU) GPUChip() else CPUChip(),
            bottomFirstTitle = "${currParams.numImages} ${stringResource(if(currParams.model.category !== Category.BERT) R.string.images else R.string.inferences)} - ${currParams.numThreads} thread${if(currParams.numThreads != 1) "s" else ""}",
            bottomSecondTitle = currParams.dataset.name,
            rows = arrayOf(
                ResultRow(stringResource(R.string.cpu_usage), "$displayCpuUsage%"),
                ResultRow(stringResource(R.string.gpu_usage), "$displayGpuUsage%"),
                ResultRow(stringResource(R.string.ram_usage), "${displayRamUsage}MB"),
            )
        )
    }
}

fun getBatteryVoltageVolts(context: Context): Float {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = context.registerReceiver(null, intentFilter)
    val result  = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
    if(result == -1) throw Exception("Erro ao calcular a tensão")
    return result / 1_000F
}

fun getBatteryCurrentAmperes(context: Context): Float {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val result = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).toFloat()
    return result / 1_000F
}



