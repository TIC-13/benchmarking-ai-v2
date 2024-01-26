package com.example.newbenchmarking.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newbenchmarking.R
import com.example.newbenchmarking.theme.Typography

@Immutable
data class Colors(
    val primary: Color,
    val secondary: Color,
    val text: Color
)

@Immutable
data class Typography(
    val title: TextStyle,
    val subtitle: TextStyle,
    val score: TextStyle,
    val scoreSubtitle: TextStyle,
    val menuButton: TextStyle,
    val tableTitle: TextStyle,
    val tableSubtitle: TextStyle,
    val tableIndex: TextStyle,
    val tableContent: TextStyle,
    val chip: TextStyle
)

val DefaultColors = Colors(
    primary = Color(0xFF862929),
    secondary = Color(0xFF023047),
    text = Color.White
)

val RobotoFamily = FontFamily(
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_mediumitalic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black)
)


val DefaultTypography = Typography(
    title = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        color = Color.White
    ),
    subtitle = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        color = Color.White
    ),
    score = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Black,
        fontSize = 96.sp,
        color = Color.White
    ),
    scoreSubtitle = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        color = Color.White
    ),
    menuButton = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        color = Color.White
    ),
    tableTitle = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        color = Color.White
    ),
    tableSubtitle = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        color = Color.White
    ),
    tableIndex = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = Color.White
    ),
    tableContent = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 13.sp,
        color = Color.White
    ),
    chip = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        color = Color.White
    )
)

val LocalAppColors = staticCompositionLocalOf {
    DefaultColors
}

val LocalAppTypography = staticCompositionLocalOf {
    DefaultTypography
}

@Composable
fun AppTheme(content: @Composable () -> Unit){
    CompositionLocalProvider(
        LocalAppColors provides DefaultColors,
        LocalAppTypography provides DefaultTypography,
        content = content
    )
}