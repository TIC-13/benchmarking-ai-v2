package com.example.newbenchmarking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult
import com.example.newbenchmarking.interfaces.models
import com.example.newbenchmarking.pages.HomeScreen
import com.example.newbenchmarking.pages.InferenceConfig
import com.example.newbenchmarking.pages.ResultScreen
import com.example.newbenchmarking.pages.RunModel
import com.example.newbenchmarking.ui.theme.NewBenchmarkingTheme
import com.example.newbenchmarking.viewModel.InferenceViewModel
import com.example.newbenchmarking.viewModel.ResultViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewBenchmarkingTheme {
                // A surface container using the 'background' color from the theme
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

@Composable
fun App(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()){

    val inferenceViewModel = InferenceViewModel()
    val resultViewModel = ResultViewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable(
            "home"
        ){
            HomeScreen(
                inferenceViewModel = inferenceViewModel,
                goToRun = { navController.navigate("runModel")},
                goToCustom = {navController.navigate("inferenceConfig")}
            )
        }
        composable(
            "inferenceConfig"
        ){
            InferenceConfig(viewModel = inferenceViewModel) {
                navController.navigate("runModel")
            }
        }
        composable(
            "runModel",
        ) {
                RunModel(viewModel = inferenceViewModel, resultViewModel = resultViewModel) {
                    navController.navigate("result")
                }
        }
        composable(
            "result",
        ){ backStackEntry ->
            backStackEntry.arguments?.let {
                ResultScreen(resultViewModel = resultViewModel){
                    navController.navigate("home")
                }
            }
        }
    }
}


