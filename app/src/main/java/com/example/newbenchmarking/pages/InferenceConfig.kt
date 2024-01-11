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

@Composable
fun InferenceConfig(modifier: Modifier = Modifier, startInference: (params: InferenceParams) -> Unit) {

    var useNNAPI by remember { mutableStateOf(false) }
    var useGPU by remember { mutableStateOf(false) }
    var numImages by remember { mutableStateOf(50) }
    var numThreads by remember { mutableStateOf(1) }
    var model by remember { mutableStateOf(models[0]) }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        SwitchSelector(
            label = "NNAPI ativa",
            isChecked = useNNAPI,
            onCheckedChange = { useNNAPI = it},
            labelColor = Color.Black
        )
        SwitchSelector(
            label = "GPU ativa",
            isChecked = useGPU,
            onCheckedChange = { useGPU = it},
            labelColor = Color.Black
        )
        SliderSelector(
            label = "Número de imagens: ${numImages}",
            value = numImages,
            onValueChange = { numImages = it.toInt() },
            rangeBottom = 15F,
            rangeUp = 400F,
            labelColor = Color.Black
        )
        SliderSelector(
            label = "Número de threads: ${numThreads}",
            value = numThreads,
            onValueChange = { numThreads = it.toInt() },
            rangeBottom = 1F,
            rangeUp = 10F,
            labelColor = Color.Black
        )
        DropdownSelector(
            "Modelo selecionado: ${model.label}",
            items = models.map {x -> x.label},
            onItemSelected = { newIndex ->
                model = models[newIndex]
            }
        )
        LargeButton(label = "Iniciar", onClick = {
            startInference(
                InferenceParams(
                    model = model,
                    useNNAPI = useNNAPI,
                    numImages = numImages,
                    useGPU = useGPU,
                    numThreads = numThreads,
                )
            )
        })
    }
}
