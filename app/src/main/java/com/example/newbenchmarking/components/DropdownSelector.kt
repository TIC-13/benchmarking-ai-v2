package com.example.newbenchmarking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownSelector(label: String, items: List<String>, onItemSelected: (index: Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(16.dp),
            color =  Color.White
        )

        Button(onClick = { expanded = !expanded }) {
            Text("Mostrar menu", color = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = false) // Optional customization
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem({ Text(text = item, color = Color.Black) }, onClick = {
                    onItemSelected(index)
                    expanded = false
                })
            }
        }
    }
}