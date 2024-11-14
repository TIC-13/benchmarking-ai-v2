package com.example.newbenchmarking.pages

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.components.AccordionProps
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.CPUChip
import com.example.newbenchmarking.components.ErrorProps
import com.example.newbenchmarking.components.GPUChip
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.NNAPIChip
import com.example.newbenchmarking.components.ResultRow
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.ResultViewModel
import com.example.newbenchmarking.requests.Inference
import com.example.newbenchmarking.requests.Phone
import com.example.newbenchmarking.requests.PostData
import com.example.newbenchmarking.requests.encryptAndPostResult

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.benchmarkResultList.observeAsState()
    if(resultList === null) return
    val results = resultList!!

    val context = LocalContext.current

    val expandedStates = rememberMutableBooleanArray(size = results.size, initialValue = false)

    BackHandler {
        back()
    }

    LaunchedEffect(Unit) {
        if(results.size > 1){
            for((index, result) in results.withIndex()){
                encryptAndPostResult(PostData(
                    Phone(
                        brand_name = Build.BRAND,
                        manufacturer = Build.MANUFACTURER,
                        phone_model = Build.MODEL,
                        total_ram = getTotalRAM(context).toInt()
                    ),
                    Inference(
                        init_speed = result.inference.load,
                        inf_speed = result.inference.average,
                        first_inf_speed = result.inference.average,
                        standard_deviation = result.inference.standardDeviation,
                        ml_model = result.params.model.label,
                        category = result.params.model.category.toString(),
                        quantization = result.params.model.quantization.toString(),
                        dataset = result.params.dataset.name,
                        num_images = result.params.numImages,
                        uses_nnapi = result.params.useNNAPI,
                        uses_gpu = result.params.useGPU,
                        num_threads = result.params.numThreads,
                        ram_usage = result.ram.getAverage().toInt(),
                        gpu_usage = result.gpu.getAverage(),
                        cpu_usage = result.cpu.getCPUUsage(),
                        gpu = null,
                        cpu = null,
                        android_id = getAndroidId(context),
                        errorMessage = result.errorMessage
                    )
                ))
            }
        }
    }

    fun onBack(){
        resultViewModel.updateInferenceResultList(arrayListOf())
        back()
    }

    BackgroundWithContent(
        modifier = Modifier.padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp),
    ) {

        Text(
            text = stringResource(id = R.string.result),
            style = LocalAppTypography.current.title
        )

        Button(
            colors = ButtonDefaults.buttonColors(LocalAppColors.current.primary),
            modifier = Modifier.background(
                color = LocalAppColors.current.primary,
                shape = RoundedCornerShape(15.dp)
        ) , onClick = { onBack() }) {
            Text(text = stringResource(id = R.string.back_to_home), color = Color.White)
        }

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            itemsIndexed(results) { index, result ->
                InferenceView(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(50.dp)),
                    topTitle = "${result.params.model.label} - ${result.params.model.quantization}",
                    subtitle = result.params.model.description,
                    bottomFirstTitle = "${result.params.numImages} ${stringResource(if(result.params.model.category !== Category.BERT) R.string.images else R.string.inferences)} - ${result.params.numThreads} thread${if(result.params.numThreads != 1) "s" else ""}",
                    bottomSecondTitle = result.params.dataset.name,
                    chip = if(result.params.useNNAPI) NNAPIChip() else if (result.params.useGPU) GPUChip() else CPUChip(),
                    rows = if(result.errorMessage === null) arrayOf(
                        ResultRow(
                            stringResource(id = R.string.initialization),
                            "${result.inference.load.toString()} ms"
                        ),
                        ResultRow(
                            stringResource(id = R.string.first_inference),
                            "${result.inference.first.toString()} ms"
                        ),
                        ResultRow(
                            stringResource(id = R.string.other_inferences), 
                            "${result.inference.average.toString()} ms"),
                    ) else null,
                    accordionProps = if(result.errorMessage === null) AccordionProps(
                        rows = arrayOf(
                            ResultRow(
                                stringResource(id = R.string.cpu_usage), 
                                formatInt(result.cpu.getAverageCPUConsumption(), "%")
                            ),
                            ResultRow(
                                stringResource(id = R.string.gpu_usage), 
                                formatInt(result.gpu.getAverage(), "%")
                            ),
                            ResultRow(
                                stringResource(id = R.string.ram_usage), 
                                "${result.ram.getAverage().toInt()}MB"
                            ),
                            ResultRow(
                                stringResource(id = R.string.cpu_peak), 
                                "${result.cpu.peak()}%"
                            ),
                            ResultRow(
                                stringResource(id = R.string.gpu_peak),
                                "${result.gpu.peak()}%"
                            ),
                            ResultRow(
                                stringResource(id = R.string.ram_peak),
                                "${result.ram.peak().toInt()}MB"
                            ),
                            if(result.inference.charsPerSecond !== null)
                                ResultRow(
                                    stringResource(id = R.string.chars_per_sec),
                                    "${result.inference.charsPerSecond} char/s"
                                )
                            else
                                ResultRow("", "")
                        ),
                        expanded = expandedStates[index].value,
                        setExpanded = {
                            for(expanded in expandedStates) {
                                expanded.value = it
                            }
                        }
                    ) else null,
                    errorProps = if(result.errorMessage !== null)
                            ErrorProps(
                                title = stringResource(id = R.string.returned_error_label), 
                                message = result.errorMessage
                            )
                        else null
                )
            }
        }
    }
}

fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

fun getTotalRAM(context: Context): Long {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    return memInfo.totalMem
}

@Composable
fun rememberMutableBooleanArray(size: Int, initialValue: Boolean): Array<MutableState<Boolean>> {
    return remember {
        Array(size) {
            mutableStateOf(initialValue)
        }
    }
}


