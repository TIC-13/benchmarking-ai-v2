package com.example.newbenchmarking.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class RadioButtonGroupOption(
    val label: String,
    val isSelected: Boolean,
    val onClick: () -> Unit
)


@Composable
fun RadioButtonGroup(modifier: Modifier = Modifier, options: List<RadioButtonGroupOption>) {
    Row(
        modifier = modifier
    ) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(8.dp)
                    .clickable { option.onClick() }
            ) {
                RadioButton(
                    selected = (option.isSelected),
                    onClick = { option.onClick() }
                )
                Text(
                    text = option.label,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}
