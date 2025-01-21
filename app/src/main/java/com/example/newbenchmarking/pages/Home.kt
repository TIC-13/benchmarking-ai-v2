package com.example.newbenchmarking.pages

import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        HomeButtonProps(
            text = stringResource(id = R.string.button_start_tests),
            icon = Icons.Default.BarChart,
            onClick = { isLoading = true }
        ),
        HomeButtonProps(
            text = stringResource(id = R.string.button_start_custom_inference),
            icon = Icons.Default.PhoneAndroid,
            onClick = { goToCustom() }
        ),
        HomeButtonProps(
            text = stringResource(id = R.string.results),
            icon = Icons.Default.Timeline,
            onClick = { goToSavedResults() }
        ),
        HomeButtonProps(
            text = stringResource(id = R.string.about),
            icon = Icons.Default.Info,
            onClick = { goToInfo() }
        ),
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
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TitleView(Modifier.padding(0.dp, 60.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                HomeScreenButtons(buttons = homeScreenButtons)
            }
        }
    }
}

@Composable
fun HomeScreenButtons(buttons: List<HomeButtonProps>) {

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
        HomeButton(
            props = props.copy(onClick = composeOnPress { props.onClick() })
        )
    }

}

data class HomeButtonProps(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier,
    val enabled: Boolean = true,
    val text: String = "Hello"
)

@Composable
fun HomeButton(
    props: HomeButtonProps
) {
    Button(
        onClick = props.onClick,
        modifier = props.modifier,
        shape = RoundedCornerShape(16.dp),
        enabled = props.enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .weight(2f),
                imageVector = props.icon,
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .weight(3f),
                text = props.text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}




