package com.example.newbenchmarking.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.LoadingScreen
import com.example.newbenchmarking.components.ScoreView
import com.example.newbenchmarking.components.TitleView
import com.example.newbenchmarking.data.getBenchmarkingTests
import com.example.newbenchmarking.data.getModels
import com.example.newbenchmarking.data.loadDatasets
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.utils.clearFolderContents
import com.example.newbenchmarking.utils.pasteAssets
import com.example.newbenchmarking.viewModel.InferenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun HomeScreen(inferenceViewModel: InferenceViewModel, goToRun: () -> Unit, goToCustom: () -> Unit) {

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun loadTestsInInternalStorage(): List<InferenceParams> {
        pasteAssets(context, destinationPath = context.filesDir.absolutePath)
        val models = getModels(File(context.filesDir, "models.yaml"))
        val datasets = loadDatasets(File(context.filesDir, "datasets.yaml"))
        return getBenchmarkingTests(models, datasets, File(context.filesDir, "tests.yaml"))
    }

    LaunchedEffect(key1 = isLoading) {
        if(isLoading){
            try {
                var tests: List<InferenceParams>? = null
                withContext(Dispatchers.IO){
                    tests = loadTestsInInternalStorage()
                }
                if(tests == null) return@LaunchedEffect
                inferenceViewModel.updateInferenceParamsList(tests!!)
                inferenceViewModel.updateFolder(context.filesDir)
                inferenceViewModel.updateAfterRun { clearFolderContents(context.filesDir) }
                goToRun()
            }catch(e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }

    val homeScreenButtons = arrayOf(
        HomeScreenButton(
            label = "Iniciar testes",
            onPress = { isLoading = true }
        ),
        HomeScreenButton(
            label = "Teste personalizado",
            onPress = { goToCustom() }
        )
    )

    if(error !== null)
        throw Exception("Erro ao carregar os modelos para o armazenamento interno")

    if(isLoading)
        return LoadingScreen()

    BackgroundWithContent(
        modifier = Modifier.padding(30.dp, 0.dp)
    ){
        Column(
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TitleView(Modifier.padding(0.dp, 120.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                for(button in homeScreenButtons){
                    Text(
                        modifier = Modifier
                            .clickable { button.onPress() },
                        text = button.label,
                        style = LocalAppTypography.current.menuButton,
                    )
                }
            }
        }
    }
}

data class HomeScreenButton(
    val label: String,
    val onPress: () -> Unit = {}
)




