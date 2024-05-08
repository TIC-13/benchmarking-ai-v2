package com.example.newbenchmarking.pages

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.newbenchmarking.components.DropdownSelector
import com.example.newbenchmarking.components.SliderSelector
import com.example.newbenchmarking.components.SwitchSelector
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.viewModel.InferenceViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.data.DATASETS
import com.example.newbenchmarking.data.getBenchmarkingTests
import com.example.newbenchmarking.data.getModels
import com.example.newbenchmarking.interfaces.Category
import kotlin.math.min

@Composable
fun InferenceConfig(modifier: Modifier = Modifier, viewModel: InferenceViewModel, startInference: () -> Unit) {

    val inferenceParams by viewModel.inferenceParamsList.observeAsState()

    val context = LocalContext.current
    val models = getModels(context)
    val benchmarkingTests = getBenchmarkingTests(context, models)

    LaunchedEffect(key1 = true) {
        viewModel.updateInferenceParamsList(listOf(benchmarkingTests[0]))
    }

    if(inferenceParams === null || inferenceParams!!.isEmpty())
        return;

    val paramsArray = inferenceParams!!

    BackgroundWithContent (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row() {
            SwitchSelector(
                label = "NNAPI ativa",
                isChecked = paramsArray[0].useNNAPI,
                onCheckedChange = { viewModel.updateInferenceParamsList(arrayListOf(paramsArray[0].copy(
                    useNNAPI = it
                ))) },
                labelColor = Color.White
            )
            SwitchSelector(
                label = "GPU ativa",
                isChecked = paramsArray[0].useGPU,
                onCheckedChange = { viewModel.updateInferenceParamsList(arrayListOf(paramsArray[0].copy(
                    useGPU = it
                )))},
                labelColor = Color.White
            )
        }
        SliderSelector(
            label = "Número de threads: ${paramsArray[0].numThreads}",
            value = paramsArray[0].numThreads,
            onValueChange = { viewModel.updateInferenceParamsList(arrayListOf(paramsArray[0].copy(
                numThreads = it.toInt()
            ))) },
            rangeBottom = 1F,
            rangeUp = 10F,
            labelColor = Color.White
        )
        DropdownSelector(
            "Modelo selecionado: ${paramsArray[0].model.label + " - " + paramsArray[0].model.quantization}",
            items = models.map {x -> x.label + " - " + x.quantization},
            onItemSelected = { newIndex ->
                viewModel.updateInferenceParamsList(arrayListOf(paramsArray[0].copy(
                    model = models[newIndex]
                )))
            }
        )
        if(paramsArray[0].model.category !== Category.BERT) {
            DropdownSelector(
                "Dataset selecionado: ${paramsArray[0].dataset.label}",
                items = DATASETS.map { x -> x.label },
                onItemSelected = { newIndex ->
                    viewModel.updateInferenceParamsList(
                        arrayListOf(
                            paramsArray[0].copy(
                                dataset = DATASETS[newIndex],
                                numImages = DATASETS[newIndex].imagesId.size / 4
                            )
                        )
                    )
                }
            )
        }
        SliderSelector(
            label = "Número de ${if(paramsArray[0].model.category === Category.BERT) "inferências" else "imagens"}: ${paramsArray[0].numImages}",
            value = paramsArray[0].numImages,
            onValueChange = { viewModel.updateInferenceParamsList(arrayListOf(paramsArray[0].copy(
                numImages = it.toInt()
            ))) },
            rangeBottom = min(15F, paramsArray[0].dataset.imagesId.size.toFloat()),
            rangeUp = paramsArray[0].dataset.imagesId.size.toFloat(),
            labelColor = Color.White
        )
        Button(onClick = startInference) {
            Text(text = "Iniciar")
        }
    }
}
