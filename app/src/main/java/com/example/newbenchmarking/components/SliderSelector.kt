package com.example.newbenchmarking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.theme.LocalAppColors

@Composable
fun SliderSelector(label: String, value: Int, onValueChange: (Float) -> Unit, rangeBottom: Float, rangeUp: Float, labelColor: Color = Color.Black) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(0.7f)) {
        Text(
            text = label,
            color = labelColor
        )
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = rangeBottom..rangeUp,
            colors = SliderDefaults.colors(
                activeTrackColor = LocalAppColors.current.secondary,
                activeTickColor = LocalAppColors.current.secondary,
                thumbColor = LocalAppColors.current.primary
            )
        )
    }
}