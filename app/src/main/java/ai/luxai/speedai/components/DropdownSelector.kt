package ai.luxai.speedai.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import ai.luxai.speedai.R

@Composable
fun DropdownSelector(modifier: Modifier = Modifier, label: String, items: List<String>, onItemSelected: (index: Int) -> Unit) {
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val selectedItemIndex = remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
            Text(
                text = label,
                modifier = Modifier.padding(bottom = 15.dp, top = 15.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.White)
                        .clickable { isDropDownExpanded.value = true }
                        .padding(5.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        text = items[selectedItemIndex.intValue],
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.drop_down_ic),
                        contentDescription = "DropDown Icon"
                    )
                }
                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = { isDropDownExpanded.value = false }
                ) {
                    items.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                isDropDownExpanded.value = false
                                selectedItemIndex.intValue = index
                                onItemSelected(index)
                            }
                        )
                    }
                }
        }
}
