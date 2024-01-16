package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.newbenchmarking.components.DropdownSelector
import com.example.newbenchmarking.components.LargeButton
import com.example.newbenchmarking.components.SliderSelector
import com.example.newbenchmarking.components.SwitchSelector
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.viewModel.InferenceViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun InferenceConfig(modifier: Modifier = Modifier, viewModel: InferenceViewModel, startInference: () -> Unit) {

    val inferenceParams by viewModel.inferenceParams.observeAsState(
        initial = InferenceParams(
            model = models[0],
            numImages = 50,
            numThreads = 1,
            useGPU = false,
            useNNAPI = false
        )
    )

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        SwitchSelector(
            label = "NNAPI ativa",
            isChecked = inferenceParams.useNNAPI,
            onCheckedChange = { viewModel.updateInferenceParams(inferenceParams.copy(useNNAPI = it)) },
            labelColor = Color.Black
        )
        SwitchSelector(
            label = "GPU ativa",
            isChecked = inferenceParams.useGPU,
            onCheckedChange = { viewModel.updateInferenceParams(inferenceParams.copy(useGPU = it))},
            labelColor = Color.Black
        )
        SliderSelector(
            label = "Número de imagens: ${inferenceParams.numImages}",
            value = inferenceParams.numImages,
            onValueChange = { viewModel.updateInferenceParams(inferenceParams.copy(numImages = it.toInt())) },
            rangeBottom = 15F,
            rangeUp = 400F,
            labelColor = Color.Black
        )
        SliderSelector(
            label = "Número de threads: ${inferenceParams.numThreads}",
            value = inferenceParams.numThreads,
            onValueChange = { viewModel.updateInferenceParams(inferenceParams.copy(numThreads = it.toInt())) },
            rangeBottom = 1F,
            rangeUp = 10F,
            labelColor = Color.Black
        )
        DropdownSelector(
            "Modelo selecionado: ${inferenceParams.model.label}",
            items = models.map {x -> x.label},
            onItemSelected = { newIndex ->
                viewModel.updateInferenceParams(inferenceParams.copy(model = models[newIndex]))
            }
        )
        LargeButton(label = "Iniciar", onClick = {
            startInference()
        })
    }
}
