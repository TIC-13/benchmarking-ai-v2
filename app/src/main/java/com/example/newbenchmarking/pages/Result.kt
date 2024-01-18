package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.viewModel.ResultViewModel

@Composable
fun ResultScreen(modifier: Modifier = Modifier, resultViewModel: ResultViewModel, back: () -> Unit) {

    val resultList by resultViewModel.inferenceResultList.observeAsState(
        initial = arrayListOf(InferenceResult(
            cpuAverage = 0F,
            gpuAverage =  0,
            ramConsumedAverage =  0F,
            inferenceTimeAverage = 0L,
            loadTime = 0L,
        ))
    )

    fun onBack(){
        resultViewModel.updateInferenceResultList(arrayListOf())
        back()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        for(result in resultList){
            ResultCategory(
                label = "Inicialização: ",
                result = result.loadTime.toString() + "ms"
            )
            ResultCategory(
                label = "Tempo médio por imagem: ",
                result = result.inferenceTimeAverage.toString() + "ms"
            )
            ResultCategory(
                label = "Utilização média de RAM: ",
                result = result.ramConsumedAverage.toInt().toString() + "MB"
            )
            ResultCategory(
                label = "Utilização média de CPU: ",
                result = "%.2f".format(result.cpuAverage) + "%"
            )
            ResultCategory(
                label = "Utilização média de GPU: ",
                result = result.gpuAverage.toString() + "%"
            )
        }

        Button(
            onClick = { onBack() },
            modifier = Modifier
                .padding(0.dp, 50.dp)
        ) {
            Text(
                text = "Voltar para a tela inicial",
                color = Color.White,
            )
        }
    }
}

@Composable
fun ResultCategory(label: String, result: String, fontSize: TextUnit = 14.sp) {
    Text(
        text = buildAnnotatedString {
            append(label)
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary))
            append(result)
        },
        fontSize = fontSize,
        color = Color.Black
    )
}