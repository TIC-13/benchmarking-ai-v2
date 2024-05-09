package com.example.newbenchmarking

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newbenchmarking.pages.HomeScreen
import com.example.newbenchmarking.pages.InferenceConfig
import com.example.newbenchmarking.pages.ResultScreen
import com.example.newbenchmarking.pages.RunModel
import com.example.newbenchmarking.theme.AppTheme
import com.example.newbenchmarking.theme.LocalAppColors
import com.example.newbenchmarking.viewModel.InferenceViewModel
import com.example.newbenchmarking.viewModel.ResultViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainScope().launch {
            if(!fileExistsInInternalStorage("models.yaml") ||
                !fileExistsInInternalStorage("datasets.yaml") ||
                !fileExistsInInternalStorage("tests.yaml"))
            {
                copyAssetsFolder()
            }
        }
        setContent {
            AppTheme {
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
fun App(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()){

    val inferenceViewModel = InferenceViewModel()
    val resultViewModel = ResultViewModel()

    val systemController = rememberSystemUiController()
    systemController.setStatusBarColor(
        color = LocalAppColors.current.primary
    )
    systemController.setNavigationBarColor(
        color = Color.Black
    )

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
                    navController.navigate("home")
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

suspend fun Context.copyAssetsFolder(folderPath: String = "", destinationPath: String = filesDir.absolutePath): Unit = withContext(Dispatchers.IO) {
    val assetManager = assets
    val files = assetManager.list(folderPath) ?: return@withContext  // List all assets at this path

    // Process each item in the current asset folder
    for (filename in files) {
        val path = if (folderPath.isEmpty()) filename else "$folderPath/$filename"
        val outFile = File(destinationPath, filename)

        try {
            // Check if the current path is a file or directory by trying to list its contents
            if (assetManager.list(path)!!.isEmpty()) {
                // It's a file (or an empty directory, which typically won't happen in assets)
                    assetManager.open(path).use { input ->
                        outFile.parentFile?.mkdirs()  // Ensure directory structure exists
                        outFile.outputStream().use { output ->
                            input.copyTo(output, 1024)
                        }
                    }

            } else {
                // It's a directory, call the function recursively
                if (!outFile.exists()) {
                    outFile.mkdirs()  // Create directory if it doesn't exist
                }
                copyAssetsFolder(path, outFile.absolutePath)
            }
        } catch (e: IOException) {
            e.printStackTrace()  // Log or handle the exception as needed
        }
    }
}

fun Context.fileExistsInInternalStorage(filename: String): Boolean {
    val file = File(filesDir, filename)
    return file.exists()
}





