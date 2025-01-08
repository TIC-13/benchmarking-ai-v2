package com.example.newbenchmarking.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.newbenchmarking.templates.ResultScreen
import com.example.newbenchmarking.utils.getAllSavedResults

@Composable
fun SavedResultScreen(
    onBack: () -> Unit
) {

    val results = getAllSavedResults()

    ResultScreen(onBack =  { onBack() }, results = results)

}