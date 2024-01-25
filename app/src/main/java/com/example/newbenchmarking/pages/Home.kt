package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.components.DropdownSelector
import com.example.newbenchmarking.components.LargeButton
import com.example.newbenchmarking.components.SliderSelector
import com.example.newbenchmarking.components.SwitchSelector
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.viewModel.InferenceViewModel

@Composable
fun HomeScreen(inferenceViewModel: InferenceViewModel, goToRun: () -> Unit, goToCustom: () -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        LargeButton(label = "Iniciar testes", onClick = {
            inferenceViewModel.updateInferenceParamsList(arrayListOf(
                InferenceParams(
                    model = models[3],
                    useNNAPI = false,
                    useGPU = false,
                    numThreads = 1,
                    numImages = 400
                ),
                InferenceParams(
                    model = models[3],
                    useNNAPI = false,
                    useGPU = true,
                    numThreads = 1,
                    numImages = 400
                ),
            ))
            goToRun()
        })
        LargeButton(label = "Teste customizado", onClick = {
            goToCustom()
        })
    }

}