package ai.luxai.speedai.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.luxai.speedai.BuildConfig
import ai.luxai.speedai.R
import ai.luxai.speedai.components.AccordionProps
import ai.luxai.speedai.components.AlertCard
import ai.luxai.speedai.components.AppTopBar
import ai.luxai.speedai.components.BackgroundWithContent
import ai.luxai.speedai.components.CPUChip
import ai.luxai.speedai.components.ErrorProps
import ai.luxai.speedai.components.GPUChip
import ai.luxai.speedai.components.InferenceView
import ai.luxai.speedai.components.NNAPIChip
import ai.luxai.speedai.components.PressableLink
import ai.luxai.speedai.components.ResultRow
import ai.luxai.speedai.components.ScrollableWithButton
import ai.luxai.speedai.hooks.useRankingAddress
import ai.luxai.speedai.interfaces.BenchmarkResult
import ai.luxai.speedai.interfaces.Category
import ai.luxai.speedai.interfaces.RunMode
import ai.luxai.speedai.interfaces.Type
import ai.luxai.speedai.pages.InferenceViewRow
import ai.luxai.speedai.pages.formatInt
import ai.luxai.speedai.pages.isNotNull
import ai.luxai.speedai.utils.navigateToUrl
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource

@Composable
fun ResultScreen(
    onBack: () -> Unit,
    results: List<BenchmarkResult>,
) {

    val context = LocalContext.current

    val rankingAddress = useRankingAddress()

    Scaffold(topBar =
    {
        AppTopBar(
            title = stringResource(id = R.string.result),
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
                buttonOnPress = { onBack() },
                buttonLabel = stringResource(id = R.string.back_to_home)
            ) {

                if(results.isEmpty()) {
                    AlertCard(text = stringResource(id = R.string.no_result_saved))
                }

                if(rankingAddress.isValid) {
                    Button(
                        modifier = Modifier.padding(top = 30.dp),
                        onClick = { navigateToUrl(context, rankingAddress.address) }
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = 10.dp),
                            painter = painterResource(R.drawable.web),
                            contentDescription = "Ranking icon"
                        )
                        Text(
                            text = stringResource(id = R.string.see_global_ranking)
                        )
                    }
                }

                for((index, result) in results.withIndex()) {
                    InferenceView(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(50.dp)),
                        topTitle = result.params.model.label + if(result.params.model.quantization!==null) " - ${result.params.model.quantization}" else "",
                        subtitle = result.params.model.description ?: "",
                        bottomFirstTitle = getBottomFirstTitle(result = result),
                        bottomSecondTitle = result.params.dataset.name,
                        chip = if (result.params.runMode == RunMode.NNAPI) NNAPIChip() else if (result.params.runMode == RunMode.GPU) GPUChip() else CPUChip(),
                        rows = if (result.errorMessage === null) listOf(
                            ResultRow(
                                stringResource(id = R.string.initialization),
                                "${result.inference.load.toString()} ms"
                            ),
                            ResultRow(
                                stringResource(id = R.string.first_inference),
                                "${result.inference.first.toString()} ms"
                            ),
                            ResultRow(
                                stringResource(id = R.string.other_inferences),
                                "${result.inference.average.toString()} ms"
                            ),
                        ) else null,
                        accordionProps = if (result.errorMessage === null) AccordionProps(
                            rows = getAccordionRows(result = result)
                        ) else null,
                        errorProps = if (result.errorMessage !== null)
                            ErrorProps(
                                title = stringResource(id = R.string.returned_error_label),
                                message = result.errorMessage
                            )
                        else null
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

@Composable
fun getAccordionRows(result: BenchmarkResult): List<ResultRow> {
    val inferenceViewRows = getAccordionInferenceViewRows(result = result).filter(::isNotNull)

    return inferenceViewRows.map { (id, label, value, suffix) ->
        ResultRow(
            label,
            formatInt(value, suffix)
        )
    }
}

@Composable
fun getAccordionInferenceViewRows(result: BenchmarkResult): Array<InferenceViewRow> {
    return arrayOf(
        InferenceViewRow(
            id = "CPU",
            label = stringResource(id = R.string.cpu_usage),
            value = result.cpu.getAverageCPUConsumption(),
            suffix = "%"
        ),
        InferenceViewRow(
            id = "GPU",
            label = stringResource(id = R.string.gpu_usage),
            value = result.gpu.getAverage(),
            suffix = "%"
        ),
        InferenceViewRow(
            id = "RAM",
            label = stringResource(id = R.string.ram_usage),
            value = result.ram.getAverage().toInt(),
            suffix = "MB"
        ),
        InferenceViewRow(
            id = "CPU_PEAK",
            label = stringResource(id = R.string.cpu_peak),
            value = result.cpu.peak(),
            suffix = "%"
        ),
        InferenceViewRow(
            id = "GPU_PEAK",
            label = stringResource(id = R.string.gpu_peak),
            value = result.gpu.peak(),
            suffix = "%"
        ),
        InferenceViewRow(
            id = "RAM_PEAK",
            label = stringResource(id = R.string.ram_peak),
            value = result.ram.peak().toInt(),
            suffix = "MB"
        ),
        if (result.inference.charsPerSecond != null) {
            InferenceViewRow(
                id = "CHARS_PER_SEC",
                label = stringResource(id = R.string.chars_per_sec),
                value = result.inference.charsPerSecond,
                suffix = " char/s"
            )
        } else {
            InferenceViewRow(
                id = "EMPTY",
                label = "",
                value = null,
                suffix = ""
            )
        }
    )
}

@Composable
fun getBottomFirstTitle(result: BenchmarkResult): String {
    val numImages = "${result.params.numImages} ${stringResource(if (result.params.model.category !== Category.BERT) R.string.images else R.string.inferences)}"
    val numThreads =
        if(result.params.type === Type.Custom)
            " - ${result.params.numThreads} thread${if (result.params.numThreads != 1) "s" else ""}"
        else ""
    return numImages + numThreads
}
