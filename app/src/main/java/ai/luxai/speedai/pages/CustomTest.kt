package ai.luxai.speedai.pages

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ai.luxai.speedai.components.DropdownSelector
import ai.luxai.speedai.components.SliderSelector
import ai.luxai.speedai.interfaces.InferenceParams
import ai.luxai.speedai.viewModel.InferenceViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ai.luxai.speedai.R
import ai.luxai.speedai.components.ActionCard
import ai.luxai.speedai.components.ActionCardBody
import ai.luxai.speedai.components.ActionCardFooter
import ai.luxai.speedai.components.ActionCardIcon
import ai.luxai.speedai.components.ActionCardTextContent
import ai.luxai.speedai.components.ActionCardTitle
import ai.luxai.speedai.components.AppTopBar
import ai.luxai.speedai.components.BackgroundWithContent
import ai.luxai.speedai.components.ErrorBoundary
import ai.luxai.speedai.components.LoadingScreen
import ai.luxai.speedai.components.RadioButtonGroup
import ai.luxai.speedai.components.RadioButtonGroupOption
import ai.luxai.speedai.components.ScrollableWithButton
import ai.luxai.speedai.data.getModelFromFile
import ai.luxai.speedai.data.loadDatasetsFromAssets
import ai.luxai.speedai.interfaces.Category
import ai.luxai.speedai.interfaces.Model
import ai.luxai.speedai.interfaces.RunMode
import ai.luxai.speedai.interfaces.Type
import ai.luxai.speedai.utils.createFolderIfNotExists
import ai.luxai.speedai.utils.setAskStoragePermission
import ai.luxai.speedai.utils.useAskStoragePermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun InferenceConfig(
    viewModel: InferenceViewModel,
    startInference: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storedPermission = useAskStoragePermission()
    val storageGranted = useCheckExternalStorage()

    var askPermission by remember {
        mutableStateOf(!storageGranted)
    }

    LaunchedEffect(key1 = storageGranted) {
        if(storageGranted)
            askPermission = false
    }

    LaunchedEffect(key1 = storedPermission) {
        if(storedPermission == false)
            askPermission = false
    }

    fun denyPermission() {
        askPermission = false
        scope.launch {
            setAskStoragePermission(context, false)
        }
    }

    if(storedPermission == null)
        return LoadingScreen()

    if(askPermission){
        return AskPermission(
            onBack = { onBack() },
            onDeny = ::denyPermission
        )
    }

    CustomTest(
        viewModel = viewModel,
        startInference = startInference,
        openPermissionPage = { askPermission = true },
        onBack = onBack,
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CustomTest(
    viewModel: InferenceViewModel,
    startInference: () -> Unit,
    openPermissionPage: () -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val storageGranted = useCheckExternalStorage()

    val canReadExternalStorage = Environment.isExternalStorageManager()

    var models by remember { mutableStateOf(viewModel.inferenceParamsList.value?.map { it.model } ?: emptyList()) }
    val datasets = remember { loadDatasetsFromAssets(context) }

    fun getParams(models: List<Model>): InferenceParams {
        return InferenceParams(
            model = models[0],
            numImages = 15,
            numThreads = 1,
            runMode = RunMode.CPU,
            dataset = datasets[0],
            type = Type.Custom
        )
    }

    var params by remember { mutableStateOf(getParams(models)) }

    LaunchedEffect(key1 = Unit) {

        val externalStorage = Environment.getExternalStorageDirectory()

        if(!canReadExternalStorage) return@LaunchedEffect

        val customModelsFolder = createFolderIfNotExists(externalStorage, "SpeedAI")

        val customModels = customModelsFolder.list()?.mapNotNull { filename ->
            val modelFile = File(customModelsFolder, filename)
            if (!modelFile.exists() || modelFile.isDirectory)
                null
            else
                getModelFromFile(modelFile)
        }

        if(customModels !== null) {
            models = customModels + models
            params = getParams(models)
        }
    }

    if(datasets.isEmpty())
        return ErrorBoundary(
            text = stringResource(id = R.string.error_no_dataset_loaded), 
            onBack = onBack
        )

    if(models.isEmpty())
        return ErrorBoundary(
            text = stringResource(id = R.string.error_no_model_loaded), 
            onBack = onBack
        )

    val radioOptions = remember(params) {
        listOf(
            Pair("CPU", RunMode.CPU),
            Pair("GPU", RunMode.GPU),
        ).map { (label, mode) ->
            RadioButtonGroupOption(
                label = label,
                isSelected = params.runMode == mode,
                onClick = { params = params.copy(runMode = mode) }
            )
        }
    }

    fun startTest() {
        viewModel.updateInferenceParamsList(listOf(params))
        startInference()
    }

    val sliderModifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.primary)
        .fillMaxWidth(0.9f)

    val dropdownModifier = Modifier
        .fillMaxWidth(0.9f)

    Scaffold(topBar =
    {
        AppTopBar(
            title = stringResource(id = R.string.custom_test),
            onBack = { onBack() }
        )
    }
    ) { paddingValues ->
        BackgroundWithContent(
            modifier = Modifier
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScrollableWithButton(
                buttonOnPress = ::startTest,
                buttonLabel = stringResource(id = R.string.start)
            ){
                Spacer(modifier = Modifier.height(10.dp))

                if(!storageGranted) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        onClick = { openPermissionPage() }
                    ) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = "storage")
                        Text(modifier = Modifier.padding(start = 5.dp), text = "Allow custom models")
                    }
                }

                RadioButtonGroup(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                    options = radioOptions
                )
                SliderSelector(
                    modifier = sliderModifier,
                    label = "${stringResource(id = R.string.number_of_threads)}: ${params.numThreads}",
                    value = params.numThreads,
                    onValueChange = { params = params.copy(numThreads = it.toInt()) },
                    rangeBottom = 1F,
                    rangeUp = 10F,
                    labelColor = Color.White
                )
                SliderSelector(
                    modifier = sliderModifier,
                    label = "${stringResource(id = R.string.number_of)} ${stringResource(if(params.model.category === Category.BERT) R.string.inferences else R.string.images)}: ${params.numImages}",
                    value = params.numImages,
                    onValueChange = { params = params.copy(numImages = it.toInt()) },
                    rangeBottom = if(params.dataset.size >= 15) 15F else 1F,
                    rangeUp = params.dataset.size.toFloat(),
                    labelColor = Color.White
                )
                DropdownSelector(
                    modifier = dropdownModifier,
                    label = stringResource(id = R.string.selected_model),
                    items = models.map {x -> x.label + if(x.quantization !== null) " - ${x.quantization}" else ""},
                    onItemSelected = { newIndex ->
                        params = params.copy(model = models[newIndex])
                    }
                )
                DropdownSelector(
                    modifier = dropdownModifier,
                    label = stringResource(id = R.string.selected_dataset),
                    items = datasets.map { x -> x.name },
                    onItemSelected = { newIndex ->
                        params = params.copy(
                            dataset = datasets[newIndex],
                            numImages = if(datasets[newIndex].size >= 15) 15 else 1
                        )
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}
@Composable
fun AskPermission(
    onBack: () -> Unit,
    onDeny: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(topBar = {
        AppTopBar(
            title = stringResource(id = R.string.custom_test),
            onBack = { onBack() }
        )
    }) { paddingValues ->
        BackgroundWithContent(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(15.dp)
                .padding(paddingValues)
        ) {
            ActionCard(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ActionCardBody(
                        modifier = Modifier
                            .weight(8f)
                            .padding(16.dp, 16.dp)
                    ) {
                        ActionCardTitle(
                            text = stringResource(id = R.string.action_card_title)
                        )
                        ActionCardIcon(
                            painter = painterResource(id = R.drawable.folder_wrench_outline),
                            description = stringResource(id = R.string.action_card_icon_description)
                        )
                        ActionCardTextContent(
                            text = stringResource(id = R.string.action_card_text_content)
                        )
                    }
                    ActionCardFooter(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable { context.requestAllFilesAccess() },
                        text = stringResource(id = R.string.action_card_footer_text)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                onClick = { onDeny() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = stringResource(id = R.string.action_card_icon_description)
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = stringResource(id = R.string.continue_with_default_models)
                )
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

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun useCheckExternalStorage(): Boolean {
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

    return granted
}