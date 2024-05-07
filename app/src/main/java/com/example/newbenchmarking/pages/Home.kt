package com.example.newbenchmarking.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.ScoreView
import com.example.newbenchmarking.components.TitleView
import com.example.newbenchmarking.data.getBenchmarkingTests
import com.example.newbenchmarking.data.getModels
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.InferenceViewModel

@Composable
fun HomeScreen(inferenceViewModel: InferenceViewModel, goToRun: () -> Unit, goToCustom: () -> Unit) {

    val context = LocalContext.current
    val models = getModels(context)
    val tests = getBenchmarkingTests(models)

    fun setDefaultModels(){
        inferenceViewModel.updateInferenceParamsList(tests)
    }

    val homeScreenButtons = arrayOf(
        HomeScreenButton(
            label = "Iniciar testes",
            onPress = {
                setDefaultModels()
                goToRun()
            }
        ),
        HomeScreenButton(
            label = "Teste personalizado",
            onPress = { goToCustom() }
        )
    )

    BackgroundWithContent(
        modifier = Modifier.padding(30.dp, 0.dp)
    ){
        Column(
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TitleView(Modifier.padding(0.dp, 120.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                for(button in homeScreenButtons){
                    Text(
                        modifier = Modifier
                            .clickable { button.onPress() },
                        text = button.label,
                        style = LocalAppTypography.current.menuButton,
                    )
                }
            }
        }
    }
}

data class HomeScreenButton(
    val label: String,
    val onPress: () -> Unit = {}
)




