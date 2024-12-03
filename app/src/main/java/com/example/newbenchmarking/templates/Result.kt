package com.example.newbenchmarking.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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
import com.example.newbenchmarking.components.ResultRow
import com.example.newbenchmarking.components.ScrollableWithButton
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.RunMode
import com.example.newbenchmarking.pages.formatInt
import com.example.newbenchmarking.pages.rememberMutableBooleanArray

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
                        rows = if (result.errorMessage === null) arrayOf(
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
                            rows = arrayOf(
                                ResultRow(
                                    stringResource(id = R.string.cpu_usage),
                                    formatInt(result.cpu.getAverageCPUConsumption(), "%")
                                ),
                                ResultRow(
                                    stringResource(id = R.string.gpu_usage),
                                    formatInt(result.gpu.getAverage(), "%")
                                ),
                                ResultRow(
                                    stringResource(id = R.string.ram_usage),
                                    "${result.ram.getAverage().toInt()}MB"
                                ),
                                ResultRow(
                                    stringResource(id = R.string.cpu_peak),
                                    "${result.cpu.peak()}%"
                                ),
                                ResultRow(
                                    stringResource(id = R.string.gpu_peak),
                                    "${result.gpu.peak()}%"
                                ),
                                ResultRow(
                                    stringResource(id = R.string.ram_peak),
                                    "${result.ram.peak().toInt()}MB"
                                ),
                                if (result.inference.charsPerSecond !== null)
                                    ResultRow(
                                        stringResource(id = R.string.chars_per_sec),
                                        "${result.inference.charsPerSecond} char/s"
                                    )
                                else
                                    ResultRow("", "")
                            )
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