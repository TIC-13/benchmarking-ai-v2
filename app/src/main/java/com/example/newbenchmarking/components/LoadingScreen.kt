package com.example.newbenchmarking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.theme.LocalAppColors

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    BackgroundWithContent(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = modifier.width(64.dp),
            color = LocalAppColors.current.primary,
            trackColor = LocalAppColors.current.secondary,
        )
    }
}
