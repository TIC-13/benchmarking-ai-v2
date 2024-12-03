package com.example.newbenchmarking.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.ErrorBoundary
import com.example.newbenchmarking.components.LoadingScreen
import com.example.newbenchmarking.components.TitleView
import com.example.newbenchmarking.data.getBenchmarkingTests
import com.example.newbenchmarking.data.getModels
import com.example.newbenchmarking.data.loadDatasets
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.utils.clearFolderContents
import com.example.newbenchmarking.utils.pasteAssets
import com.example.newbenchmarking.viewModel.InferenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeScreen(
    inferenceViewModel: InferenceViewModel,
    goToRun: () -> Unit,
    goToCustom: () -> Unit,
    onBack: () -> Unit,
    goToInfo: () -> Unit,
    goToSavedResults: () -> Unit
) {

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun loadTestsInInternalStorage(): List<InferenceParams> {
        pasteAssets(context, destinationPath = context.filesDir.absolutePath)
        val models = getModels(
            file = File(context.filesDir, "models.yaml")
        )
        val datasets = loadDatasets(
            file = File(context.filesDir, "datasets.yaml")
        )
        return getBenchmarkingTests(
            models = models,
            datasets = datasets,
            file = File(context.filesDir, "tests.yaml"),
        )
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

    val homeScreenButtons = listOf(
        HomeScreenButtonProps(
            label = stringResource(id = R.string.button_start_tests),
            onPress = { isLoading = true }
        ),
        HomeScreenButtonProps(
            label = stringResource(id = R.string.button_start_custom_inference),
            onPress = { goToCustom() }
        ),
        HomeScreenButtonProps(
            label = stringResource(id = R.string.results),
            onPress = { goToSavedResults() }
        ),
        HomeScreenButtonProps(
            label = stringResource(id = R.string.about),
            onPress = { goToInfo() }
        )
    )

    if(error !== null)
        return ErrorBoundary(
            text = stringResource(id = R.string.error_model_not_loaded), 
            onBack = onBack
        )

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
                HomeScreenButtons(buttons = homeScreenButtons)
            }
        }
    }
}

@Composable
fun HomeScreenButtons(buttons: List<HomeScreenButtonProps>) {

    val isExecuting = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isExecuting) {
        delay(1000)
        isExecuting.value = false
    }

    fun composeOnPress(onPress: () -> Unit): () -> Unit {
        return fun() {
            if (!isExecuting.value) {
                isExecuting.value = true
                onPress()
            }
        }
    }

    for (props in buttons) {
        val (label, onPress) = props
        HomeScreenButton(
            props = HomeScreenButtonProps(
                label = label,
                onPress = composeOnPress(onPress)
            )
        )
    }

}


@Composable
fun HomeScreenButton(props: HomeScreenButtonProps) {
    val (label, onPress) = props

    Text(
        modifier = Modifier
            .clickable { onPress() },
        text = label,
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge
    )
}


data class HomeScreenButtonProps(
    val label: String,
    val onPress: () -> Unit = {}
)




