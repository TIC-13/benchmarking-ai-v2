package ai.luxai.speedai.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import ai.luxai.speedai.R
import ai.luxai.speedai.components.AccordionItem
import ai.luxai.speedai.components.AccordionText
import ai.luxai.speedai.components.AppTopBar
import ai.luxai.speedai.components.BackgroundWithContent
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.ui.platform.LocalContext
import ai.luxai.speedai.components.PressableLink
import ai.luxai.speedai.utils.navigateToUrl

val textSectionModifier: Modifier = Modifier.padding(15.dp, 30.dp, 15.dp, 15.dp)
val linkModifier: Modifier = Modifier.padding(15.dp, 10.dp, 0.dp, 0.dp)

@Composable
fun InfoScreen(
    goBack: () -> Unit,
    goToLicenses: () -> Unit
) {

    val context = LocalContext.current

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
                    modifier = textSectionModifier,
                    title = stringResource(R.string.about_app_title),
                    content = stringResource(R.string.about_app_content)
                )

                AccordionItem(title = stringResource(id = R.string.how_works_title)) {
                    AccordionItem(title = stringResource(id = R.string.how_works_gpu_title)) {
                        AccordionText(text = stringResource(id = R.string.how_works_gpu))
                    }
                    AccordionItem(title = stringResource(id = R.string.how_works_ram_title)) {
                        AccordionText(text = stringResource(id = R.string.how_works_ram))
                    }
                    AccordionItem(title = stringResource(id = R.string.how_works_cpu_title)) {
                        AccordionText(text = stringResource(id = R.string.how_works_cpu))
                    }
                }

                TextSection(
                    modifier = textSectionModifier,
                    title = stringResource(id = R.string.about_luxai_title),
                    content = stringResource(id = R.string.about_luxai),
                    titleIcon = Icons.Default.Camera,
                    componentAfterTitle = {
                        PressableLink(
                            modifier = linkModifier,
                            text = stringResource(id = R.string.website_label),
                            onPress = { navigateToUrl(context, "https://luxai.cin.ufpe.br") }
                        )
                    }
                )

                TextSection(
                    modifier = textSectionModifier,
                    title = stringResource(id = R.string.go_to_licenses_title),
                    titleIcon = Icons.Default.LibraryBooks,
                    content = "",
                    componentAfterTitle = {
                        PressableLink(
                            modifier = linkModifier,
                            text = stringResource(id = R.string.go_to_licenses_link),
                            onPress = { goToLicenses() }
                        )
                    }
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
    titleIcon: ImageVector? = null,
    componentAfterTitle: @Composable() (ColumnScope.() -> Unit)? = null
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

        if (componentAfterTitle != null) {
            componentAfterTitle()
        }

        Text(
            modifier = Modifier.padding(15.dp),
            text = content,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}