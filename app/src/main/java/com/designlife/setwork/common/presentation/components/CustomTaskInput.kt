package com.designlife.setwork.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.TaskItemLabelColor
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.commonStyleSize
import com.designlife.setwork.ui.theme.taskItemStyle
import com.designlife.setwork.ui.theme.taskItemStyleSize

@Composable
fun CustomTaskInput(
    value : String,
    placeholder : String,
    onValueChange : (value : String) -> Unit,
    multiline : Boolean = false
) {
    BasicTextField(
        value = value,
        maxLines = if (multiline) 5 else 1,
        cursorBrush = SolidColor(ButtonPrimary.value),
        textStyle = TextStyle(
            fontSize = commonStyleSize.value,
            color = TypographyColor.value
        ),
        onValueChange = {
            onValueChange(it)
        },
    ){ innerTextField ->

        Column(
            modifier = Modifier
                .padding(start = 6.dp)
                .fillMaxWidth()
                .height(30.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (value.isBlank() || value.isEmpty()){
                    Text(
                        text = placeholder,
                        style = taskItemStyle.value.copy(color = TypographyColor.value),
                        fontSize = taskItemStyleSize.value
                    )
                }
                innerTextField()
            }
        }

    }
}