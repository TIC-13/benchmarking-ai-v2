package com.example.newbenchmarking.pages

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.ErrorBoundary
import com.example.newbenchmarking.components.LoadingScreen
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
fun InferenceConfig(modifier: Modifier = Modifier, viewModel: InferenceViewModel, startInference: () -> Unit, onBack: () -> Unit) {

    var granted by remember { mutableStateOf(Environment.isExternalStorageManager()) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted = Environment.isExternalStorageManager()
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(Unit) {
        granted = Environment.isExternalStorageManager()
    }

    if(!granted){
        return AskPermission()
    }

    CustomTest(
        viewModel = viewModel,
        startInference = startInference,
        onBack = onBack
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CustomTest(modifier: Modifier = Modifier, viewModel: InferenceViewModel, startInference: () -> Unit, onBack: () -> Unit) {

    val context = LocalContext.current
    val canReadExternalStorage = Environment.isExternalStorageManager()
    val externalStorage = Environment.getExternalStorageDirectory()
    val speedAIFolder = createFolderIfNotExists(externalStorage, "SpeedAI")

    var loadedModels by remember { mutableStateOf<List<Model>?>(null) }
    var loadedDatasets by remember { mutableStateOf<List<Dataset>?>(null) }
    var loadingFails by remember { mutableStateOf(emptyList<String>()) }

    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        loadedModels = null
        loadedDatasets = null
        loadingFails = emptyList()

        if(!canReadExternalStorage) return@LaunchedEffect

        try {
            if(!fileExists(speedAIFolder, "models.yaml") ||
                !fileExists(speedAIFolder, "datasets.yaml")
            ){
                pasteAssets(context, destinationPath = speedAIFolder.absolutePath)
            }
            withContext(Dispatchers.IO) {
                loadedModels = getModels(
                    file = File(speedAIFolder, "models.yaml"),
                    onError = { e, id -> loadingFails = loadingFails + "Erro ao carregar o modelo de ID $id: ${e.message}" }
                )
                loadedDatasets = loadDatasets(
                    file = File(speedAIFolder, "datasets.yaml"),
                    onError = { e, id -> loadingFails = loadingFails + "Erro ao carregar o dataset de ID $id: ${e.message}" }
                )
            }
        }catch(e: Exception) {
            error = e.message
        }
    }

    if(!canReadExternalStorage)
        return ErrorBoundary(text = "Permissão para ler armazenamento interno não concedida", onBack = onBack)

    if(error !== null)
        return ErrorBoundary(text = "Erro ao carregar arquivos para armazenamento externo: ${error}", onBack = onBack)

    if(loadedModels == null || loadedDatasets == null)
        return LoadingScreen()

    val models = loadedModels!!
    val datasets = loadedDatasets!!

    if(datasets.isEmpty())
        return ErrorBoundary(text = "Nenhum dataset foi carregado. O arquivo datasets.yaml deve estar vazio ou mal-formatado", onBack = onBack)

    if(models.isEmpty())
        return ErrorBoundary(text = "Nenhum modelo foi carregado. O arquivo models.yaml deve estar vazio ou mal-formatado", onBack = onBack)

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
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            for(loadingFail in loadingFails){
                LoadingFailView(text = loadingFail)
            }
        }
        Row {
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

@Composable
fun AskPermission() {

    val context = LocalContext.current

    BackgroundWithContent (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(15.dp)
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(50.dp)
        ) {
            Text(
                text = "Para usar essa função, o aplicativo precisa de permissão para ler os arquivos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Button(onClick = { context.requestAllFilesAccess() }) {
                Text(text = "Conceder permissão")
            }
        }
    }
}

private fun Context.requestAllFilesAccess() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        startActivity(intent)
    }
}

@Composable
fun LoadingFailView(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(20)
    ) {
        Text(
            modifier = Modifier.padding(15.dp),
            text = text,
            color = MaterialTheme.colorScheme.onError,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
