package ai.luxai.speedai.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.luxai.speedai.components.AppTopBar
import ai.luxai.speedai.components.BackgroundWithContent
import ai.luxai.speedai.R

const val licenseContent =
                            "DeepLab v3 model - Apache 2.0\n" +
                            "EfficientNet model - Apache 2.0\n" +
                            "ESRGAN model - MIT\n" +
                            "MobileNetv3 model - Apache 2.0\n" +
                            "SSD-MobileNetv3 model - Apache 2.0\n" +
                            "Yolov4 model - MIT"

@Composable
fun LicensesScreen(
    onBack: () -> Unit,
) {

    Scaffold(topBar =
    {
        AppTopBar(
            title = stringResource(id = R.string.licenses_title),
            onBack = { onBack() }
        )
    }
    ) { paddingValues ->
        BackgroundWithContent(
            modifier = Modifier
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(top = 30.dp, start = 15.dp),
                text = licenseContent,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium,
            )

        }
    }
}