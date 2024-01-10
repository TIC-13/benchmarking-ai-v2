package com.example.newbenchmarking.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newbenchmarking.R

@Composable
fun LargeButton(modifier: Modifier = Modifier, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .clip(RoundedCornerShape(20))
            .background(color = MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = modifier)
        Text(
            modifier = modifier.fillMaxWidth(0.7f),
            text = label,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 16.sp,
        )
        Image(
            painter = painterResource(id = R.drawable.i0),
            contentDescription = "lighting icon",
            alignment = Alignment.CenterEnd
        )
    }
}