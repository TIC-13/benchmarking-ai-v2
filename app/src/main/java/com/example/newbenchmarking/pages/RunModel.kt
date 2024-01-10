package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.newbenchmarking.components.LargeButton
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.machineLearning.runImageClassification
import com.example.newbenchmarking.utils.getImage
import com.example.newbenchmarking.utils.getImagesIdList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage

@Composable
fun RunModel(modifier: Modifier = Modifier, params: InferenceParams, goBack: () -> Unit) {

    val context = LocalContext.current
    var loadingLable by remember { mutableStateOf("Carregando...") }

    val imagesIdList = getImagesIdList(params.numImages)
    val tensorImages = imagesIdList.map { TensorImage.fromBitmap(getImage(id = it)) }

    LaunchedEffect(Unit){
        var result: Pair<Long, Long>
        withContext(Dispatchers.IO){
            result = runImageClassification(context, "efficientNetFP32.tflite", tensorImages, params.useNNAPI)
        }
        loadingLable = "Inicialização: ${result.first}, Média: ${result.second}"
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = loadingLable,
            modifier = modifier
        )
        LargeButton(label = "Voltar", onClick = goBack)
    }

}