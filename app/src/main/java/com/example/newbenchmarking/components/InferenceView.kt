package com.example.newbenchmarking.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.theme.LocalAppTypography


@Composable
fun InferenceView(
    modifier: Modifier = Modifier,
    params: InferenceParams,
    cpuUsage: String,
    gpuUsage: String,
    ramUsage: String,
    initTime: String? = null,
    infTime: String? = null,
    showInfoButton: Boolean = false
) {

    val rows = arrayOf(
        TableRow("Inicialização", initTime),
        TableRow("Tempo de inferência", infTime),
        TableRow("Uso de CPU", cpuUsage),
        TableRow("Uso de GPU", gpuUsage),
        TableRow("Uso de RAM", ramUsage)
    )

    var infoActive by remember { mutableStateOf(false) }

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
                                text = params.model.label + " - " + params.model.quantization,

                                style = LocalAppTypography.current.tableTitle
                            )
                            Text(
                                text = params.model.description,
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
                            if(params.useNNAPI){
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth(0.2F)
                                        .fillMaxHeight(),
                                    color = LocalAppColors.current.nnapi,
                                    text = "NNAPI"
                                )
                            }else if(params.useGPU){
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth(0.2F)
                                        .fillMaxHeight(),
                                    color = LocalAppColors.current.gpu,
                                    text = "GPU"
                                )
                            }else{
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth(0.2F)
                                        .fillMaxHeight(),
                                    text = "CPU"
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
                Text(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 20.dp),
                    text = "${params.numImages} Imagens - ${params.numThreads} thread" +
                            if(params.numThreads == 1) "" else "s",
                    style = LocalAppTypography.current.tableIndex
                )
                Text(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 20.dp),
                    text = params.dataset.label,
                    style = LocalAppTypography.current.tableIndex
                )
                for(row in rows){
                    TextRow(row)
                }
            }
        }
    }
    if(infoActive) {
        Modal(
            onConfirmation = { infoActive = false },
            dialogTitle = params.model.label,
            dialogText = params.model.longDescription
        )
    }
}

data class TableRow(
    val index: String,
    val value: String?
)
@Composable
fun TextRow(row: TableRow) {
    if(row.value !== null)
        Row (
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .padding(0.dp, 0.dp, 0.dp, 10.dp)
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7F)
            ){
                Text(
                    text = row.index,
                    style = LocalAppTypography.current.tableIndex
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    text = row.value,
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
        tint= color
    )
}


@Composable
fun Chip(modifier: Modifier = Modifier, text: String, color: Color = LocalAppColors.current.secondary){
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = text,
            style = LocalAppTypography.current.chip
        )
    }

}