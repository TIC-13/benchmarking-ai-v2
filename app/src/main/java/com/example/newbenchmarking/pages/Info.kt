package com.example.newbenchmarking.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.components.AccordionItem
import com.example.newbenchmarking.components.AccordionText
import com.example.newbenchmarking.components.AppTopBar
import com.example.newbenchmarking.components.BackgroundWithContent
import androidx.compose.material.icons.filled.Camera

@Composable
fun InfoScreen(goBack: () -> Unit) {

    Scaffold(topBar =
    {
        AppTopBar(
            title = stringResource(id = R.string.about),
            onBack = { goBack() }
        )
    }
    ) {
        paddingValues ->
        BackgroundWithContent (
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                TextSection(
                    modifier = Modifier.padding(15.dp, 30.dp, 15.dp, 15.dp),
                    title = "What does this app do?",
                    content = "This app benchmarks LLMs"
                )

                AccordionItem(title = "How the benchmarking works") {
                    AccordionItem(title = "CPU Measurement") {
                        AccordionText(text = "dsalçkhfçasdlkf")
                    }
                    AccordionItem(title = "GPU Measurement") {
                        AccordionText(text = "dsalçkhfçasdlkf")
                    }
                    AccordionItem(title = "RAM Measurement") {
                        AccordionText(text = "dsalçkhfçasdlkf")
                    }
                }

                TextSection(
                    modifier = Modifier.padding(15.dp, 30.dp, 15.dp, 15.dp),
                    title = "About Lux.AI",
                    content = "Lux.AI is a project",
                    titleIcon = Icons.Default.Camera
                )

            }
        }
    }


}

@Composable
fun TextSection(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    titleIcon: ImageVector? = null
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(titleIcon !== null){
                Icon(
                    modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp),
                    imageVector = titleIcon,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "LuxAI Icon"
                )
            }
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            modifier = Modifier.padding(15.dp),
            text = content,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}