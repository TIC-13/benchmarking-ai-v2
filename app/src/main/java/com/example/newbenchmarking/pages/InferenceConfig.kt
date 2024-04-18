package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.data.DATASETS
import com.example.newbenchmarking.data.DEFAULT_PARAMS
import com.example.newbenchmarking.data.MODELS
import com.example.newbenchmarking.interfaces.Category
import kotlin.math.min

@Composable
fun InferenceConfig(modifier: Modifier = Modifier, viewModel: InferenceViewModel, startInference: () -> Unit) {

    val inferenceParams by viewModel.inferenceParamsList.observeAsState(
        initial = arrayListOf(
            DEFAULT_PARAMS.params
        )
    )

    BackgroundWithContent (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row() {
            SwitchSelector(
                label = "NNAPI ativa",
                isChecked = inferenceParams[0].useNNAPI,
                onCheckedChange = { viewModel.updateInferenceParamsList(arrayListOf(inferenceParams[0].copy(
                    useNNAPI = it
                ))) },
                labelColor = Color.White
            )
            SwitchSelector(
                label = "GPU ativa",
                isChecked = inferenceParams[0].useGPU,
                onCheckedChange = { viewModel.updateInferenceParamsList(arrayListOf(inferenceParams[0].copy(
                    useGPU = it
                )))},
                labelColor = Color.White
            )
        }
        SliderSelector(
            label = "Número de threads: ${inferenceParams[0].numThreads}",
            value = inferenceParams[0].numThreads,
            onValueChange = { viewModel.updateInferenceParamsList(arrayListOf(inferenceParams[0].copy(
                numThreads = it.toInt()
            ))) },
            rangeBottom = 1F,
            rangeUp = 10F,
            labelColor = Color.White
        )
        DropdownSelector(
            "Modelo selecionado: ${inferenceParams[0].model.label}",
            items = MODELS.map {x -> x.label},
            onItemSelected = { newIndex ->
                viewModel.updateInferenceParamsList(arrayListOf(inferenceParams[0].copy(
                    model = MODELS[newIndex]
                )))
            }
        )
        if(inferenceParams[0].model.category !== Category.LANGUAGE) {
            DropdownSelector(
                "Dataset selecionado: ${inferenceParams[0].dataset.label}",
                items = DATASETS.map { x -> x.label },
                onItemSelected = { newIndex ->
                    viewModel.updateInferenceParamsList(
                        arrayListOf(
                            inferenceParams[0].copy(
                                dataset = DATASETS[newIndex],
                                numImages = DATASETS[newIndex].imagesId.size / 4
                            )
                        )
                    )
                }
            )
        }
        SliderSelector(
            label = "Número de ${if(inferenceParams[0].model.category === Category.LANGUAGE) "inferências" else "imagens"}: ${inferenceParams[0].numImages}",
            value = inferenceParams[0].numImages,
            onValueChange = { viewModel.updateInferenceParamsList(arrayListOf(inferenceParams[0].copy(
                numImages = it.toInt()
            ))) },
            rangeBottom = min(15F, inferenceParams[0].dataset.imagesId.size.toFloat()),
            rangeUp = inferenceParams[0].dataset.imagesId.size.toFloat(),
            labelColor = Color.White
        )
        Button(onClick = startInference) {
            Text(text = "Iniciar")
        }
    }
}
