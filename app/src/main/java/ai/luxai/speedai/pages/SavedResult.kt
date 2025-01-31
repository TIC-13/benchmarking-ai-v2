package ai.luxai.speedai.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import ai.luxai.speedai.templates.ResultScreen
import ai.luxai.speedai.utils.getAllSavedResults

@Composable
fun SavedResultScreen(
    onBack: () -> Unit
) {

    val results = getAllSavedResults()

    ResultScreen(onBack =  { onBack() }, results = results)

}