package com.example.newbenchmarking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography


data class ChipProps(
    val text: String,
    val color: Color
)

data class Row(
    val label: String,
    val text: String
)

data class InfoContent(
    val title: String,
    val subtitle: String
)

@Composable
fun InferenceView(
    modifier: Modifier = Modifier,
    topTitle: String,
    subtitle: String,
    chip: ChipProps? = null,
    infoContent: InfoContent? = null,
    bottomFirstTitle: String? = null,
    bottomSecondTitle: String? = null,
    rows: Array<Row>,
) {

    var infoActive by remember { mutableStateOf(false) }
    val showInfoButton = infoContent !== null

    Box(
        modifier = modifier
            .background(LocalAppColors.current.primary)
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom

        ) {
            Column(
                modifier = Modifier
                    .padding(0.dp, 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if(showInfoButton){
                            Spacer(modifier = Modifier.fillMaxWidth(0.2F))
                        }
                        Column(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth(if (showInfoButton) 0.75F else 1F),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(5.dp, 5.dp),
                                text = topTitle,

                                style = LocalAppTypography.current.tableTitle
                            )
                            Text(
                                text = subtitle,
                                style = LocalAppTypography.current.tableSubtitle
                            )
                            
                        }
                        if(showInfoButton){
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(1F)
                                    .clickable { infoActive = true },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ){
                                InfoIcon()
                            }
                        }
                    }
                        Row(
                            modifier = Modifier.height(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(chip !== null) {
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth(0.2F)
                                        .fillMaxHeight(),
                                    color = chip.color,
                                    text = chip.text
                                )
                            }
                        }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalAppColors.current.secondary)
                    .padding(0.dp, 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if(bottomFirstTitle !== null){
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 20.dp),
                        text = bottomFirstTitle,
                        style = LocalAppTypography.current.tableIndex
                    )
                }
                if(bottomSecondTitle !== null){
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 20.dp),
                        text = bottomSecondTitle,
                        style = LocalAppTypography.current.tableIndex
                    )
                }
                for (row in rows) {
                        TextRow(row)
                }
            }
        }
    }
    if(infoActive && infoContent !== null) {
        Modal(
            onConfirmation = { infoActive = false },
            dialogTitle = infoContent.title,
            dialogText = infoContent.subtitle
        )
    }
}

@Composable
fun TextRow(row: Row) {

    Row(
        modifier = Modifier
            .fillMaxWidth(0.7F)
            .padding(0.dp, 0.dp, 0.dp, 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6F)
        ) {
            Text(
                text = row.label,
                style = LocalAppTypography.current.tableIndex
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = row.text,
                style = LocalAppTypography.current.tableContent
            )
        }
    }
}

    @Composable
    fun InfoIcon(color: Color = LocalAppColors.current.text) {
        Icon(
            painter = painterResource(id = R.drawable.help_circle),
            "",
            tint = color
        )
    }

    @Composable
    fun Chip(
        modifier: Modifier = Modifier,
        text: String,
        color: Color = LocalAppColors.current.secondary
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(50.dp))
                .background(color),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = LocalAppTypography.current.chip
            )
        }

    }

    fun formatTime(time: Int?): String {
        if (time == null) return "Não medido"
        return if (time >= 1000)
            String.format("%.2f", time.toDouble() / 1000) + "s"
        else
            time.toString() + "ms"
    }

    fun formatInt(percent: Int?, complemento: String, returnsZero: Boolean = true): String {
        return if (percent != null && (percent != 0 || returnsZero))
            "$percent$complemento"
        else
            "Não medida"
    }

@Composable
fun NNAPIChip(): ChipProps {
    return ChipProps(
        text = "NNAPI",
        color = LocalAppColors.current.nnapi
    )
}

@Composable
fun CPUChip(): ChipProps {
    return ChipProps(
        text = "CPU",
        color = LocalAppColors.current.secondary
    )
}

@Composable
fun GPUChip(): ChipProps {
    return ChipProps(
        text = "GPU",
        color = LocalAppColors.current.gpu
    )
}


