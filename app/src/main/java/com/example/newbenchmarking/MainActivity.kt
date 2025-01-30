package com.example.newbenchmarking

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newbenchmarking.pages.BenchmarkResultScreen
import com.example.newbenchmarking.pages.HomeScreen
import com.example.newbenchmarking.pages.InferenceConfig
import com.example.newbenchmarking.pages.InfoScreen
import com.example.newbenchmarking.pages.RunModel
import com.example.newbenchmarking.pages.SavedResultScreen
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.viewModel.InferenceViewModel
import com.example.newbenchmarking.viewModel.ResultViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.newbenchmarking.pages.LicensesScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.example.compose.AppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {

    val inferenceViewModel = InferenceViewModel()
    val resultViewModel = ResultViewModel()

    val systemController = rememberSystemUiController()
    systemController.setStatusBarColor(
        color = LocalAppColors.current.primary
    )
    systemController.setNavigationBarColor(
        color = Color.Black
    )


    var lastBackPressedTime by remember { mutableStateOf(0L) }
    val backPressInterval = 1000

    fun debounceBackPress(action: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressedTime > backPressInterval) {
            lastBackPressedTime = currentTime
            action()
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                inferenceViewModel = inferenceViewModel,
                goToRun = { navController.navigate("runModel") },
                goToCustom = { navController.navigate("inferenceConfig") },
                goToInfo = { navController.navigate("info") },
                goToSavedResults = { navController.navigate("savedResult") },
                onBack = { debounceBackPress { navController.popBackStack() } }
            )
        }
        composable("inferenceConfig") {
            InferenceConfig(
                viewModel = inferenceViewModel,
                startInference = { navController.navigate("runModel") },
                onBack = { debounceBackPress { navController.popBackStack() } }
            )
        }
        composable("runModel") {
            RunModel(viewModel = inferenceViewModel, resultViewModel = resultViewModel) {
                navController.navigate("result")
            }
        }
        composable("result") { backStackEntry ->
            backStackEntry.arguments?.let {
                BenchmarkResultScreen(resultViewModel = resultViewModel) {
                    debounceBackPress { navController.popBackStack("home", false) }
                }
            }
        }
        composable("savedResult") {
            SavedResultScreen() {
                debounceBackPress { navController.popBackStack() }
            }
        }
        composable("info") {
            InfoScreen(
                goBack = { debounceBackPress { navController.popBackStack() }},
                goToLicenses = { navController.navigate("licenses") }
            )
        }
        composable("licenses") {
            LicensesScreen {
                debounceBackPress { navController.popBackStack() }
            }
        }
    }
}



