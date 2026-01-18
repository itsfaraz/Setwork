package com.designlife.justdo.container.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.taskItemStyle
import com.designlife.justdo.ui.theme.taskItemStyleSize

@Composable
fun CategoryItemComponent(
    categoryTitle : String,
    onTitleUpdate : (newTitle : String) -> Unit,
    onDeleteEvent : () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {}
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(value = categoryTitle, onValueChange = {onTitleUpdate(it)}, textStyle = taskItemStyle.value.copy(color = TypographyColor.value, fontSize = taskItemStyleSize.value))
        IconButton(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(20.dp),
            onClick = {onDeleteEvent()}
        ){
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon", tint = Color.Red)
        }
    }
}

