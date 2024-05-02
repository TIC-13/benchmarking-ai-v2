package com.example.newbenchmarking.pages

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
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
import com.example.newbenchmarking.data.DEFAULT_PARAMS
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.ResultViewModel
import com.example.newbenchmarking.requests.Inference
import com.example.newbenchmarking.requests.Phone
import com.example.newbenchmarking.requests.PostData
import com.example.newbenchmarking.requests.postResult

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.benchmarkResultList.observeAsState(
        initial = arrayListOf(
            DEFAULT_PARAMS
        )
    )

    val context = LocalContext.current

    val expandedStates = rememberMutableBooleanArray(size = resultList.size, initialValue = false)
    var expandButtonState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if(resultList.size > 1){
            for((index, result) in resultList.withIndex()){
                postResult(PostData(
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
                        dataset = result.params.dataset.label,
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
            text = "Resultado",
            style = LocalAppTypography.current.title
        )

        Button(
            colors = ButtonDefaults.buttonColors(LocalAppColors.current.primary),
            modifier = Modifier.background(
                color = LocalAppColors.current.primary,
                shape = RoundedCornerShape(15.dp)
        ) , onClick = { onBack() }) {
            Text(text = "Voltar para tela inicial", color = Color.White)
        }

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            itemsIndexed(resultList) { index, result ->
                InferenceView(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(50.dp)),
                    topTitle = "${result.params.model.label} - ${result.params.model.quantization}",
                    subtitle = result.params.model.description,
                    bottomFirstTitle = "${result.params.numImages} ${if(result.params.model.category !== Category.BERT) "imagens" else "inferências"} - ${result.params.numThreads} thread${if(result.params.numThreads != 1) "s" else ""}",
                    bottomSecondTitle = result.params.dataset.label,
                    chip = if(result.params.useNNAPI) NNAPIChip() else if (result.params.useGPU) GPUChip() else CPUChip(),
                    rows = if(result.errorMessage === null) arrayOf(
                        ResultRow("Inicialização", "${result.inference.load.toString()} ms"),
                        ResultRow("Primeira inferência", "${result.inference.first.toString()} ms"),
                        ResultRow("Outras inf. (média)", "${result.inference.average.toString()} ms"),
                    ) else null,
                    accordionProps = if(result.errorMessage === null) AccordionProps(
                        rows = arrayOf(
                            ResultRow("Uso de CPU", "${result.cpu.getAverageCPUConsumption()}%"),
                            ResultRow("Uso de GPU", "${result.gpu.getAverage()}%"),
                            ResultRow("Uso de RAM", "${result.ram.getAverage().toInt()}MB"),
                            ResultRow("Pico de CPU", "${result.cpu.peak()}%"),
                            ResultRow("Pico de GPU", "${result.gpu.peak()}%"),
                            ResultRow("Pico de RAM", "${result.ram.peak().toInt()}MB"),
                        ),
                        expanded = expandedStates[index].value,
                        setExpanded = {
                            for(expanded in expandedStates) {
                                expanded.value = it
                            }
                        }
                    ) else null,
                    errorProps = if(result.errorMessage !== null)
                            ErrorProps(title = "Erro retornado", message = result.errorMessage)
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


