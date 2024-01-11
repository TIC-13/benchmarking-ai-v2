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
import com.example.newbenchmarking.pages.InferenceConfig
import com.example.newbenchmarking.pages.ResultScreen
import com.example.newbenchmarking.pages.RunModel
import com.example.newbenchmarking.ui.theme.NewBenchmarkingTheme

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
    NavHost(navController = navController, startDestination = "inferenceConfig") {
        composable(
            "inferenceConfig"
        ){
            InferenceConfig{ (modelPath, useNNAPI, numImages) ->
                navController.navigate("runModel/$modelPath/$useNNAPI/$numImages")
            }
        }
        composable(
            "runModel/{modelFile}/{useNNAPI}/{numImages}",
            arguments = listOf(
                navArgument("modelFile") {type = NavType.StringType},
                navArgument("useNNAPI") {type = NavType.BoolType},
                navArgument("numImages") {type = NavType.IntType }
            ),

        ) { backStackEntry ->
            backStackEntry.arguments?.let {
                RunModel(Modifier, InferenceParams(
                    modelFile = it.getString("modelFile"),
                    useNNAPI = it.getBoolean("useNNAPI"),
                    numImages = it.getInt("numImages"))
                ) {
                    (
                        cpuAverage,
                        gpuAverage,
                        ramConsumedAverage,
                        inferenceTimeAverage,
                        loadTime) ->
                            navController.navigate("result/$loadTime/$inferenceTimeAverage/$cpuAverage/$gpuAverage/$ramConsumedAverage")
                }
            }
        }
        composable(
            "result/{load}/{time}/{cpu}/{gpu}/{ram}",
            arguments = listOf(
                navArgument("load") {type = NavType.LongType},
                navArgument("time") {type = NavType.LongType},
                navArgument("cpu") {type = NavType.FloatType},
                navArgument("gpu") {type = NavType.FloatType},
                navArgument("ram") {type = NavType.FloatType},
            )
        ){ backStackEntry ->
            backStackEntry.arguments?.let {
                ResultScreen(result =
                    InferenceResult(
                        loadTime = it.getLong("load"),
                        inferenceTimeAverage = it.getLong("time"),
                        ramConsumedAverage = it.getFloat("ram"),
                        cpuAverage = it.getFloat("cpu"),
                        gpuAverage = it.getFloat("gpu")
                    )
                ) {navController.navigate("inferenceConfig")}
            }
        }
    }
}


