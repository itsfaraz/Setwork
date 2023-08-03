package com.designlife.justdo.container.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.ColorPaletteItem1
import com.designlife.justdo.ui.theme.ColorPaletteItem2
import com.designlife.justdo.ui.theme.ColorPaletteItem3
import com.designlife.justdo.ui.theme.ColorPaletteItem4
import com.designlife.justdo.ui.theme.ColorPaletteItem5
import com.designlife.justdo.ui.theme.ColorPaletteItem6
import com.designlife.justdo.ui.theme.ColorPaletteItem7
import com.designlife.justdo.ui.theme.ColorPaletteItem8
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.taskItemStyle

@Composable
fun CustomRadioItem(
    title : String,
    isSelected : Boolean,
    isRepeatMode : Boolean = false,
    isDummyCategory : Boolean = false,
    categoryName : String = "",
    onCategoryNameChange : (value : String) -> Unit = {},
    colorCode : Color = PrimaryBackgroundColor,
    colorPickerEvent : () -> Unit = {},
    colorPickerSelectedColor : Color = TaskItemLabelColor,
    onCategoryInsertEvent : () -> Unit = {},
    onSelectedEvent : () -> Unit
) {
    var color by remember {
        mutableStateOf(colorCode)
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {

        if (!isRepeatMode){
            Box(modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(20.dp)
                .clickable {
                    if (isDummyCategory) {
                        colorPickerEvent()
                    }
                }
                .background(
                    color = if (isDummyCategory) colorPickerSelectedColor else colorCode,
                    shape = RoundedCornerShape(100)
                )
            ) {

            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            if (isDummyCategory){
                CategoryTextField(
                    modifier = Modifier.fillMaxWidth(.9f),
                    textValue = categoryName,
                    onTextChange = {
                        onCategoryNameChange(it)
                    }
                )
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier
                    .padding(end = 10.dp)
                    .size(20.dp)
                    .clickable {
                        onCategoryInsertEvent()
                    })
            }else{
                Text(
                    modifier = Modifier.padding(end = 10.dp),
                    text = title,
                    style = taskItemStyle
                )
                RadioButton(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(20.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = ButtonPrimary,
                        unselectedColor = TaskItemLabelColor
                    ),
                    selected = isSelected,
                    onClick = {
                        onSelectedEvent()
                    }
                )
            }
        }
    }
}

