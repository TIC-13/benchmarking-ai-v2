package com.example.newbenchmarking.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.theme.LocalAppTypography

@Composable
fun TitleView(modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speed.AI",
                style = LocalAppTypography.current.title
            )
            Image(
                painter = painterResource(id = R.drawable.lightning),
                contentDescription = "Icon in the shape of lightning"
            )
        }

        Text(
            text = "Benchmarking de modelos de machine learning",
            style = LocalAppTypography.current.subtitle
        )
    }
}