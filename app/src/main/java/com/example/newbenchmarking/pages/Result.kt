package com.example.newbenchmarking.pages

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.data.DEFAULT_PARAMS
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.ResultViewModel
import com.example.newbenchmarking.requests.Inference
import com.example.newbenchmarking.requests.Phone
import com.example.newbenchmarking.requests.PostData
import com.example.newbenchmarking.requests.postResult
import kotlin.math.floor


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.inferenceResultList.observeAsState(
        initial = arrayListOf(
            DEFAULT_PARAMS
        )
    )

    val context = LocalContext.current

    LaunchedEffect(Unit) {

        data class BenchPair(
            val label: String,
            val content: String,
        )

        fun getAndroidId(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        fun getTotalRAM(): Long {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            return memInfo.totalMem
        }

        if(resultList.size > 1){
            for((index, result) in resultList.withIndex()){
                postResult(PostData(
                    Phone(
                        brand_name = Build.BRAND,
                        manufacturer = Build.MANUFACTURER,
                        phone_model = Build.MODEL,
                        total_ram = getTotalRAM().toInt()
                    ),
                    Inference(
                        init_speed = result.loadTime?.toInt(),
                        inf_speed = if(result.inferenceTimeAverage !== null) result.inferenceTimeAverage.toInt() else null,
                        first_inf_speed = result.firstInference?.toInt(),
                        standard_deviation = result.standardDeviation?.toInt(),
                        ml_model = result.params.model.label,
                        category = result.params.model.category.toString(),
                        quantization = result.params.model.quantization.toString(),
                        dataset = result.params.dataset.label,
                        num_images = result.params.numImages,
                        uses_nnapi = result.params.useNNAPI,
                        uses_gpu = result.params.useGPU,
                        num_threads = result.params.numThreads,
                        ram_usage = result.ramConsumedAverage?.toInt(),
                        gpu_usage = if(result.gpuAverage != 0 ) result.gpuAverage else null,
                        cpu_usage = result.cpuAverage,
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

        Text(text = "Resultados", style = LocalAppTypography.current.title)

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
            items(resultList) { result ->
                InferenceView(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(50.dp)),
                    params = result.params,
                    cpuUsage = result.cpuAverage,
                    cpuPeak = result.cpuPeak,
                    gpuUsage = result.gpuAverage,
                    gpuPeak = result.gpuPeak,
                    ramUsage = result.ramConsumedAverage?.toInt(),
                    ramPeak = result.ramPeak?.toInt(),
                    initTime = result.loadTime?.toInt(),
                    firstInfTime = result.firstInference?.toInt(),
                    standardDeviation = result.standardDeviation?.toInt(),
                    infTime = result.inferenceTimeAverage?.toInt(),
                    showInfoButton = true,
                    errorMessage = result.errorMessage,
                )
            }
        }
    }
}


