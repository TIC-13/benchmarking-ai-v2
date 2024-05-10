package com.example.newbenchmarking.pages

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.data.getModels
import com.example.newbenchmarking.data.loadDatasets
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Dataset
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.utils.createFolderIfNotExists
import com.example.newbenchmarking.utils.fileExists
import com.example.newbenchmarking.utils.pasteAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun InferenceConfig(modifier: Modifier = Modifier, viewModel: InferenceViewModel, startInference: () -> Unit) {

    val context = LocalContext.current
    val canReadExternalStorage = Environment.isExternalStorageManager()
    val externalStorage = Environment.getExternalStorageDirectory()
    val speedAIFolder = createFolderIfNotExists(externalStorage, "SpeedAI")

    var loadedModels by remember { mutableStateOf<List<Model>?>(null) }
    var loadedDatasets by remember { mutableStateOf<List<Dataset>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        loadedModels = null
        loadedDatasets = null

        if(!canReadExternalStorage) return@LaunchedEffect

        try {
            if(!fileExists(speedAIFolder, "models.yaml") ||
                !fileExists(speedAIFolder, "datasets.yaml") ||
                !fileExists(speedAIFolder, "tests.yaml")
            ){
                pasteAssets(context, destinationPath = speedAIFolder.absolutePath)
            }
            withContext(Dispatchers.IO) {
                loadedModels = getModels(File(speedAIFolder, "models.yaml"))
                loadedDatasets = loadDatasets(File(speedAIFolder, "datasets.yaml"))
            }
        }catch(e: Exception) {
            error = e.message
        }
    }

    if(!canReadExternalStorage)
        throw Error("Não tem permissão de ler armazenamento externo")

    if(error !== null)
        throw Error("Erro ao carregar arquivos para armazenamento externo: ${error}")

    if(loadedModels == null || loadedDatasets == null)
        return BackgroundWithContent {
            //isLoading
        }

    val models = loadedModels!!
    val datasets = loadedDatasets!!

    if(datasets.isEmpty())
        throw Error("Nenhum dataset foi carregado")

    if(models.isEmpty())
        throw Error("Nenhum modelo foi carregado")

    var params by remember { mutableStateOf(InferenceParams(
        model = models[0],
        numImages = 15,
        numThreads = 1,
        useGPU = false,
        useNNAPI = false,
        dataset = datasets[0]
    )) }

    fun startTest() {
        viewModel.updateInferenceParamsList(listOf(params))
        viewModel.updateFolder(speedAIFolder)
        startInference()
    }

    BackgroundWithContent (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row() {
            SwitchSelector(
                label = "NNAPI ativa",
                isChecked = params.useNNAPI,
                onCheckedChange = { params = params.copy(useNNAPI = !params.useNNAPI) },
                labelColor = Color.White
            )
            SwitchSelector(
                label = "GPU ativa",
                isChecked = params.useGPU,
                onCheckedChange = { params = params.copy(useGPU = !params.useGPU)},
                labelColor = Color.White
            )
        }
        SliderSelector(
            label = "Número de threads: ${params.numThreads}",
            value = params.numThreads,
            onValueChange = { params = params.copy(numThreads = it.toInt()) },
            rangeBottom = 1F,
            rangeUp = 10F,
            labelColor = Color.White
        )
        DropdownSelector(
            "Modelo selecionado: ${params.model.label + " - " + params.model.quantization}",
            items = models.map {x -> x.label + " - " + x.quantization},
            onItemSelected = { newIndex ->
                params = params.copy(model = models[newIndex])
            }
        )
        DropdownSelector(
            "Dataset selecionado: ${params.dataset.name}",
            items = datasets.map { x -> x.name },
            onItemSelected = { newIndex ->
                params = params.copy(
                    dataset = datasets[newIndex],
                    numImages = if(datasets[newIndex].size >= 15) 15 else 1
                )
            }
        )
        SliderSelector(
            label = "Número de ${if(params.model.category === Category.BERT) "inferências" else "imagens"}: ${params.numImages}",
            value = params.numImages,
            onValueChange = { params = params.copy(numImages = it.toInt()) },
            rangeBottom = if(params.dataset.size >= 15) 15F else 1F,
            rangeUp = params.dataset.size.toFloat(),
            labelColor = Color.White
        )
        Button(onClick = ::startTest) {
            Text(text = "Iniciar")
        }
    }
}
