package com.designlife.setwork.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.domain.entities.Todo
import com.designlife.setwork.common.presentation.components.rippleClickable
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.UIComponentBackground
import com.designlife.setwork.ui.theme.taskItemStyle
import com.designlife.setwork.ui.theme.taskItemStyleSize
import com.designlife.setwork.ui.theme.todoListHeight

@Composable
fun TodoItem(
    color : Color,
    todo : Todo,
    onTodoEvent : () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = todoListHeight.value)
            .fillMaxWidth()
            .clip(RoundedCornerShape(40))
            .rippleClickable{ onTodoEvent() }
            .height(50.dp),
        shape = RoundedCornerShape(40),
        backgroundColor = UIComponentBackground.value,
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = UIComponentBackground.value, shape = RoundedCornerShape(20)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(100))
                    .background(color = if (todo.isCompleted) color else Color.Transparent)
                    .border(width = 2.dp, color = color, shape = RoundedCornerShape(100)),
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted){
                    Icon(modifier = Modifier
                        .size(15.dp)
                        .clip(RoundedCornerShape(100)),imageVector = Icons.Default.Check, contentDescription = "Done", tint = Color.White)
                }
            }
            Text(
                text = if (todo.title.length > 40) "${todo.title.substring(0,38)} ..." else todo.title,
                style = taskItemStyle.value.copy(
                    fontSize = taskItemStyleSize.value,
                    color = TypographyColor.value
                ),
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
        }
    }
}