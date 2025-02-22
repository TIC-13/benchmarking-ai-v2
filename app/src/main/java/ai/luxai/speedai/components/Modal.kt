package ai.luxai.speedai.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ai.luxai.speedai.R
import ai.luxai.speedai.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modal(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector? = null,
) {
    AlertDialog(
        icon = {
            if(icon !== null)
                Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle, color = LocalAppColors.current.text)
        },
        text = {
            Text(text = dialogText, color = LocalAppColors.current.text)
        },
        onDismissRequest = {
            onConfirmation()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(
                    stringResource(id = R.string.close),
                    color = LocalAppColors.current.text
                )
            }
        },
        containerColor = LocalAppColors.current.primary
    )
}