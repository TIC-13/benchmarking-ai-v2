package ai.luxai.speedai.pages

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ai.luxai.speedai.R
import ai.luxai.speedai.components.BackgroundWithContent
import ai.luxai.speedai.components.TitleView
import ai.luxai.speedai.data.getBenchmarkingTestsFromAssets
import ai.luxai.speedai.data.getModelsFromAssets
import ai.luxai.speedai.data.loadDatasetsFromAssets
import ai.luxai.speedai.hooks.useNoActionOnDelay
import ai.luxai.speedai.hooks.useRankingAddress
import ai.luxai.speedai.interfaces.InferenceParams
import ai.luxai.speedai.utils.navigateToUrl
import ai.luxai.speedai.viewModel.InferenceViewModel
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

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

    val run = useNoActionOnDelay()

    val (_, loadTests) = useLoadTests()

    val rankingAddress = useRankingAddress()

    fun runLoadTests(context: Context, then: () -> Unit) {
        loadTests(context){ inferenceParamsList ->
            if (inferenceParamsList != null) {
                inferenceViewModel.updateInferenceParamsList(inferenceParamsList)
            }
            then()
        }
    }

    fun startBenchmarking() {
        runLoadTests(context) {
            goToRun()
        }
    }

    fun startCustom() {
        runLoadTests(context) {
            goToCustom()
        }
    }

    val homeScreenButtons = listOfNotNull(
        HomeButtonProps(
            text = stringResource(id = R.string.button_start_tests),
            icon = VectorIcon(Icons.Default.BarChart),
            onClick = { startBenchmarking() }
        ),
        HomeButtonProps(
            text = stringResource(id = R.string.button_start_custom_inference),
            icon = VectorIcon(Icons.Default.PhoneAndroid),
            onClick = { startCustom() }
        ),
        HomeButtonProps(
            text = stringResource(id = R.string.results),
            icon = VectorIcon(Icons.Default.Timeline),
            onClick = { goToSavedResults() }
        ),
        if (rankingAddress.isValid)
            HomeButtonProps(
                text = stringResource(id = R.string.global_ranking),
                icon = PainterIcon(painterResource(id = R.drawable.web)),
                onClick = { navigateToUrl(context, rankingAddress.address) }
            )
        else null,
        HomeButtonProps(
            text = stringResource(id = R.string.about),
            icon = VectorIcon(Icons.Default.Info),
            onClick = { goToInfo() }
        )
    )

    BackgroundWithContent(
        modifier = Modifier.padding(15.dp, 0.dp)
    ){
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TitleView(Modifier.padding(0.dp, 30.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                for (props in homeScreenButtons) {
                    HomeButton(
                        props = props.copy(onClick = { run { props.onClick() } } )
                    )
                }
            }
        }
    }
}

sealed class HomeButtonIcon
data class VectorIcon(val icon: ImageVector): HomeButtonIcon()
data class PainterIcon(val icon: Painter): HomeButtonIcon()

data class HomeButtonProps(
    val icon: HomeButtonIcon,
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier,
    val enabled: Boolean = true,
    val text: String = "Hello"
)

@Composable
fun HomeButton(
    props: HomeButtonProps
) {
    val iconModifier = Modifier.size(24.dp)
    val iconContentDescription = null

    Button(
        onClick = {props.onClick()},
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
                .padding(0.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when(props.icon) {
                is VectorIcon ->
                    Icon(
                        modifier = iconModifier.weight(2f),
                        imageVector = props.icon.icon,
                        contentDescription = iconContentDescription,
                    )
                is PainterIcon ->
                    Icon(
                        modifier = iconModifier.weight(2f),
                        painter = props.icon.icon,
                        contentDescription = iconContentDescription,
                    )
            }
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

data class LoadTestState(
    val isLoading: Boolean,
    val loadTests: (Context, ((List<InferenceParams>?) -> Unit)?) -> Unit,
    val inferenceParams: List<InferenceParams>?
)

@Composable
fun useLoadTests(): LoadTestState {

    var isLoading by remember { mutableStateOf(false) }
    var inferenceParams by remember { mutableStateOf<List<InferenceParams>?>(null) }

    fun loadTests(
        context: Context,
        afterLoad: ((params: List<InferenceParams>?) -> Unit)? = null
    ) {
        isLoading = true

        //pasteAssets(context, destinationPath = context.filesDir.absolutePath)

        val models = getModelsFromAssets(context)
        val datasets = loadDatasetsFromAssets(context)

        inferenceParams = getBenchmarkingTestsFromAssets(
            context = context,
            models = models,
            datasets = datasets
        )

        afterLoad?.invoke(inferenceParams)

        isLoading = false

    }

    return LoadTestState(
        isLoading = isLoading,
        loadTests = ::loadTests,
        inferenceParams = inferenceParams
    )
}




