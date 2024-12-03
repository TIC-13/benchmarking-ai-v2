package com.example.newbenchmarking.pages

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import com.example.newbenchmarking.components.AppTopBar
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.CPUChip
import com.example.newbenchmarking.components.ErrorProps
import com.example.newbenchmarking.components.GPUChip
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.NNAPIChip
import com.example.newbenchmarking.components.ResultRow
import com.example.newbenchmarking.components.ScrollableWithButton
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.RunMode
import com.example.newbenchmarking.interfaces.Type
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.ResultViewModel
import com.example.newbenchmarking.requests.Inference
import com.example.newbenchmarking.requests.Phone
import com.example.newbenchmarking.requests.PostData
import com.example.newbenchmarking.requests.encryptAndPostResult
import com.example.newbenchmarking.templates.ResultScreen
import com.example.newbenchmarking.utils.saveResultLocally

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BenchmarkResultScreen(
    modifier: Modifier = Modifier,
    resultViewModel: ResultViewModel,
    back: () -> Unit
) {

    val resultList by resultViewModel.benchmarkResultList.observeAsState()
    if(resultList === null) return
    val results = resultList!!

    val context = LocalContext.current

    fun onBack(){
        resultViewModel.updateInferenceResultList(arrayListOf())
        back()
    }

    BackHandler {
        onBack()
    }

    LaunchedEffect(Unit) {
            for((index, result) in results.withIndex()){

                if(result.params.type == Type.Custom)
                    continue

                saveResultLocally(context, result)

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
                        uses_nnapi = result.params.runMode == RunMode.NNAPI,
                        uses_gpu = result.params.runMode == RunMode.GPU,
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

    ResultScreen(onBack = ::onBack, results = results)
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


