package com.example.newbenchmarking.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.newbenchmarking.templates.ResultScreen
import com.example.newbenchmarking.utils.getAllSavedResults

@Composable
fun SavedResultScreen(
    navController: NavController
) {

    val results = getAllSavedResults()

    fun onBack() {
        navController.popBackStack()
    }

    ResultScreen(onBack = ::onBack, results = results)

}