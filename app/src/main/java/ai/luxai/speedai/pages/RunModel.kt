package ai.luxai.speedai.pages

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.luxai.speedai.BuildConfig
import ai.luxai.speedai.R
import ai.luxai.speedai.benchmark.CpuUsage
import ai.luxai.speedai.benchmark.GpuUsage
import ai.luxai.speedai.benchmark.RamUsage
import ai.luxai.speedai.components.AppTopBar
import ai.luxai.speedai.components.BackgroundWithContent
import ai.luxai.speedai.components.CPUChip
import ai.luxai.speedai.components.GPUChip
import ai.luxai.speedai.components.InferenceView
import ai.luxai.speedai.components.NNAPIChip
import ai.luxai.speedai.components.ResultRow
import ai.luxai.speedai.interfaces.BenchmarkResult
import ai.luxai.speedai.interfaces.Category
import ai.luxai.speedai.interfaces.Inference
import ai.luxai.speedai.interfaces.InferenceParams
import ai.luxai.speedai.interfaces.RunMode
import ai.luxai.speedai.interfaces.Type
import ai.luxai.speedai.machineLearning.runTfLiteModel
import ai.luxai.speedai.utils.getBitmapsFromAssetsFolder
import ai.luxai.speedai.viewModel.InferenceViewModel
import ai.luxai.speedai.viewModel.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.reflect.KSuspendFunction0


data class InferenceViewRow(
    val id: String,
    val label: String,
    val value: Int?,
    val suffix: String
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RunModel(
    viewModel: InferenceViewModel,
    resultViewModel: ResultViewModel,
    goToResults: () -> Unit
) {

    val context = LocalContext.current
    val inferencesList by viewModel.inferenceParamsList.observeAsState()
    val afterRun by viewModel.afterRun.observeAsState()

    val resultsList by resultViewModel.benchmarkResultList.observeAsState()

    if(inferencesList === null || inferencesList!!.isEmpty()) return
    val paramsList = inferencesList!!

    var cpuUsage by remember { mutableStateOf(CpuUsage()) }
    var displayCpuUsage by remember { mutableStateOf<Int?>(null) }

    var ramUsage by remember { mutableStateOf(RamUsage()) }
    var displayRamUsage by remember { mutableIntStateOf(0) }

    var gpuUsage by remember { mutableStateOf(GpuUsage())}
    var displayGpuUsage by remember { mutableStateOf<Int?>(0) }

    var currModelIndex by remember { mutableIntStateOf(1) }
    var currParams by remember { mutableStateOf(paramsList[0]) }

    var currImageIndex by remember { mutableIntStateOf(0) }

    val counter = useCounter()

    val (delayActive, activateDelay) = useDelay()

    LaunchedEffect(Unit){
        for((index, inferenceParams) in paramsList.withIndex()){

            activateDelay()

            var result = Inference()
            var errorMessage: String? = null
            var images: List<Bitmap>? = null

            withContext(Dispatchers.IO){

                currImageIndex = 0
                currParams = inferenceParams
                currModelIndex = index + 1

                try {
                    result = if(inferenceParams.model.category === Category.BERT) {
                        throw Exception("Categoria BERT descontinuada")
                    }else{
                        images = getBitmapsFromAssetsFolder(
                            context = context,
                            folderName = inferenceParams.dataset.folderName,
                            numBitmaps = inferenceParams.numImages
                        )
                        runTfLiteModel(context, inferenceParams, images!!) {
                            currImageIndex = it
                        }
                    }
                }catch (e: Exception){
                        Log.d("Error", e.toString())
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
        afterRun?.let { it() }
        goToResults()
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while(true){
                delay(5)
                if(!delayActive){
                    cpuUsage.calculateCPUUsage()
                    ramUsage.calculateUsage()
                    gpuUsage.calculateUsage()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while(true){
                delay(500)
                displayCpuUsage = cpuUsage.getCPUUsage()
                displayRamUsage = ramUsage.get()
                displayGpuUsage = gpuUsage.get()
            }
        }
    }

    BackHandler(enabled = true){}

    val inferenceViewRows = arrayOf(
        if (BuildConfig.DEBUG) {
            InferenceViewRow(
                id = "CPU",
                label = stringResource(R.string.cpu_usage),
                value = displayCpuUsage ?: 0,
                suffix = "%"
            )
        } else null,
        InferenceViewRow(
            id = "GPU",
            label = stringResource(R.string.gpu_usage),
            value = displayGpuUsage ?: 0,
            suffix = "%"
        ),
        InferenceViewRow(
            id = "RAM",
            label = stringResource(R.string.ram_usage),
            value = displayRamUsage,
            suffix = "MB"
        )
    ).filterNotNull()

    Scaffold(topBar =
    {
        AppTopBar(
            title = "${" ".repeat(counter)}Benchmarking${".".repeat(counter)}",
        )
    }
    ) {
        paddingValues ->

        BackgroundWithContent (
            modifier = Modifier.padding(paddingValues),
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
                    text = "${stringResource(R.string.model)} $currModelIndex ${stringResource(R.string.out_of)} ${inferencesList!!.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            InferenceView(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)),
                topTitle = currParams.model.label + if(currParams.model.quantization !== null) " - ${currParams.model.quantization}" else "",
                subtitle = currParams.model.description ?: "",
                chip = if(currParams.runMode == RunMode.NNAPI) NNAPIChip() else if (currParams.runMode == RunMode.GPU) GPUChip() else CPUChip(),
                bottomFirstTitle = getBottomFirstTitle(currParams, currImageIndex),
                bottomSecondTitle = currParams.dataset.name,
                rows = inferenceViewRows.map { row ->
                    ResultRow(row.label, formatInt(row.value, row.suffix))
                },

                )
        }
    }
}

fun formatInt(value: Int?, suffix: String): String {
    if(value == null) return "-"
    return "${value}${suffix}"
}

fun isNotNull(row: InferenceViewRow): Boolean {
    return row.value !== null
}

@Composable
fun useCounter(): Int {

    var counter by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.IO) {
            while(true){
                delay(1000)
                if(counter >= 3) counter = 0 else counter ++
            }
        }
    }

    return counter
}

data class DelayProps(
    val delayActive: Boolean,
    val activateDelay: KSuspendFunction0<Unit>
)

@Composable
fun useDelay(delayTime: Long = 2000): DelayProps {

    var delayActive by remember {
        mutableStateOf(false)
    }

    suspend fun activateDelay() {
        delayActive = true
        delay(delayTime)
        delayActive = false
    }

    return DelayProps(delayActive, activateDelay = ::activateDelay)
}

@Composable
fun getBottomFirstTitle(currParams: InferenceParams, currImageIndex: Int): String {
    val imageCount =
        "$currImageIndex/${currParams.numImages} ${stringResource(if (currParams.model.category !== Category.BERT) R.string.images else R.string.inferences)}"
    val numThreads = if (currParams.type == Type.Custom)
        " - ${currParams.numThreads} thread${if (currParams.numThreads != 1) "s" else ""}${
            " ".repeat(
                currImageIndex.toString().length - 1
            )
        }"
    else ""

    return imageCount + numThreads
}




