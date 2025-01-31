package ai.luxai.speedai.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.luxai.speedai.R
import ai.luxai.speedai.theme.LocalAppColors
import ai.luxai.speedai.theme.LocalAppTypography


data class ChipProps(
    val text: String,
    val color: Color
)

data class ResultRow(
    val label: String,
    val text: String
)

data class InfoContent(
    val title: String,
    val subtitle: String
)

data class AccordionProps(
    val rows: List<ResultRow>,
)

data class ErrorProps(
    val message: String,
    val title: String,
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
    rows: List<ResultRow>? = null,
    accordionProps: AccordionProps? = null,
    errorProps: ErrorProps? = null
) {

    var infoActive by remember { mutableStateOf(false) }
    val showInfoButton = infoContent !== null

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
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
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
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
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(0.dp, 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if(bottomFirstTitle !== null){
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 20.dp),
                        text = bottomFirstTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                if(bottomSecondTitle !== null){
                    Text(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 0.dp, 20.dp),
                        text = bottomSecondTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                if(rows !== null){
                    for (row in rows) {
                        TextRow(row)
                    }
                }
                if(accordionProps !== null){
                    Accordion(accordionProps)
                }
                if(errorProps !== null){
                    ErrorDisplay(errorProps)
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
fun ErrorDisplay(
    errorProps: ErrorProps
) {

    var showErrorActive by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(1F)
            .clickable { showErrorActive = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ){
        Text(
            modifier = Modifier.padding(5.dp, 0.dp),
            text = errorProps.title,
            style = LocalAppTypography.current.tableContent
        )
        InfoIcon()
    }
    if(showErrorActive) {
        Modal(
            onConfirmation = { showErrorActive = false },
            dialogTitle = stringResource(id = R.string.returned_error_label),
            dialogText = errorProps.message
        )
    }

}

@Composable
fun AccordionHeader(
    title: String = "Header",
    isExpanded: Boolean = false,
    onTapped: () -> Unit = {}
) {

    val degrees = if (isExpanded) 180f else 0f

    Row(
        modifier = Modifier
            .clickable { onTapped() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
            Icon(
                Icons.Outlined.ArrowDropDown,
                contentDescription = "arrow-down",
                modifier = Modifier.rotate(degrees),
                tint = White
            )
    }
}

@Composable
fun Accordion(accordionProps: AccordionProps) {

    val (rows) = accordionProps

    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        AccordionHeader(
            title = stringResource(id =
                if(!expanded)
                    R.string.show_more
                else
                    R.string.show_less),
            isExpanded = expanded
        ){
            expanded = !expanded
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                for(row in rows) {
                    TextRow(row)
                }
            }
        }
    }
}

@Composable
fun TextRow(row: ResultRow) {

    Row(
        modifier = Modifier
            .fillMaxWidth(0.6F)
            .padding(0.dp, 0.dp, 0.dp, 5.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(0.7F),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = row.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.weight(0.1F))
        Column(
            modifier = Modifier
                .weight(0.4F),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = row.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

    @Composable
    fun InfoIcon(color: Color = MaterialTheme.colorScheme.onPrimary) {
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
        color: Color = MaterialTheme.colorScheme.secondary
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
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

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
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun GPUChip(): ChipProps {
    return ChipProps(
        text = "GPU",
        color = LocalAppColors.current.gpu
    )
}


