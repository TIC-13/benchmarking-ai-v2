package com.example.newbenchmarking.templates

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.components.AccordionProps
import com.example.newbenchmarking.components.AlertCard
import com.example.newbenchmarking.components.AppTopBar
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.CPUChip
import com.example.newbenchmarking.components.ErrorProps
import com.example.newbenchmarking.components.GPUChip
import com.example.newbenchmarking.components.InferenceView
import com.example.newbenchmarking.components.NNAPIChip
import com.example.newbenchmarking.components.OpenLinkInBrowser
import com.example.newbenchmarking.components.ResultRow
import com.example.newbenchmarking.components.ScrollableWithButton
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.RunMode
import com.example.newbenchmarking.pages.InferenceViewRow
import com.example.newbenchmarking.pages.formatInt
import com.example.newbenchmarking.pages.isNotNull

@Composable
fun ResultScreen(
    onBack: () -> Unit,
    results: List<BenchmarkResult>,
) {

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

                OpenLinkInBrowser(
                    icon = Icons.Default.Link,
                    modifier = Modifier.padding(0.dp, if(results.isEmpty()) 0.dp else 25.dp, 0.dp, 0.dp),
                    text = stringResource(id = R.string.global_ranking),
                    uri = "http://cinsoftex.drayddns.com:8082/simpleRanking"
                )

                for((index, result) in results.withIndex()) {
                    InferenceView(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(50.dp)),
                        topTitle = "${result.params.model.label} - ${result.params.model.quantization}",
                        subtitle = result.params.model.description,
                        bottomFirstTitle = "${result.params.numImages} ${stringResource(if (result.params.model.category !== Category.BERT) R.string.images else R.string.inferences)} - ${result.params.numThreads} thread${if (result.params.numThreads != 1) "s" else ""}",
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
