package com.example.newbenchmarking.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp


@Composable
fun OpenLinkInBrowser(
    modifier: Modifier = Modifier,
    text: String,
    uri: String,
    icon: ImageVector? = null // Optional ImageVector for the icon
) {
    val context = LocalContext.current
    val annotatedString = AnnotatedString(text)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically

    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Content description for accessibility
                modifier = Modifier.size(24.dp), // Adjust size as needed
                tint = Color(0xFF77cff8) // Match text color for consistency
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        ClickableText(
            text = annotatedString,
            style = TextStyle(
                color = Color(0xFF77cff8),
                textDecoration = TextDecoration.Underline
            ),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                context.startActivity(intent)
            }
        )
    }
}
