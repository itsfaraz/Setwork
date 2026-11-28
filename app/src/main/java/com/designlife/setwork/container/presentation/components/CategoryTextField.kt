package com.designlife.setwork.container.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import com.designlife.setwork.ui.theme.ButtonPrimary
import com.designlife.setwork.ui.theme.TaskItemLabelColor
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.headerStyle

@Composable
fun CategoryTextField(
    modifier : Modifier,
    textValue : String,
    onTextChange : (value : String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = textValue,
        onValueChange = {
            onTextChange(it)
        },
        readOnly = false,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text
        ),
        textStyle = headerStyle.value.copy(color = TypographyColor.value),
        cursorBrush = SolidColor(ButtonPrimary.value)
    ){ basicField ->
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (textValue.isBlank() && textValue.isEmpty()){
                Text(
                    text = "Add Category Name",
                    color = TaskItemLabelColor.value
                )
            }else
                basicField()
        }
    }
}