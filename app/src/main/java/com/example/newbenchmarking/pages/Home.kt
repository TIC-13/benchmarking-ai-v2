package com.example.newbenchmarking.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newbenchmarking.components.BackgroundWithContent
import com.example.newbenchmarking.components.ScoreView
import com.example.newbenchmarking.components.TitleView
import com.example.newbenchmarking.data.BENCHMARKING_TESTS
import com.example.newbenchmarking.theme.LocalAppTypography
import com.example.newbenchmarking.viewModel.InferenceViewModel

@Composable
fun HomeScreen(inferenceViewModel: InferenceViewModel, goToRun: () -> Unit, goToCustom: () -> Unit) {

    fun setDefaultModels(){
        inferenceViewModel.updateInferenceParamsList(BENCHMARKING_TESTS)
    }

    val homeScreenButtons = arrayOf(
        HomeScreenButton(
            label = "Ver resultados"
        ),
        HomeScreenButton(
            label = "Reiniciar teste",
            onPress = {
                setDefaultModels()
                goToRun()
            }
        ),
        HomeScreenButton(
            label = "Ranking"
        ),
        HomeScreenButton(
            label = "Teste personalizado",
            onPress = { goToCustom() }
        )
    )

    BackgroundWithContent(
        modifier = Modifier.padding(30.dp, 0.dp)
    ){
        TitleView(
            modifier = Modifier
                .fillMaxWidth(0.7F)
        )
        ScoreView()
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

data class HomeScreenButton(
    val label: String,
    val onPress: () -> Unit = {}
)




