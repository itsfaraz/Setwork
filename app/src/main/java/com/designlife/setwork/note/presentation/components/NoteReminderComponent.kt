package com.designlife.setwork.note.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.setwork.R
import com.designlife.setwork.common.presentation.components.rippleClickable
import com.designlife.setwork.ui.theme.TaskItemLabelColor
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.taskItemLabelStyle
import com.designlife.setwork.ui.theme.taskItemLabelStyleSize
import com.designlife.setwork.ui.theme.taskItemStyle
import com.designlife.setwork.ui.theme.taskItemStyleSize

@Composable
fun NoteReminderComponent(
    dateText: String,
    timeText: String,
    onDateChange: () -> Unit,
    onTimeChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_schedule),
            contentDescription = "Item Icon",
            modifier = Modifier.size(14.dp),
            tint = TaskItemLabelColor.value
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxWidth(),
                text = "Date",
                style = taskItemLabelStyle.value,
                fontSize = taskItemLabelStyleSize.value
            )
            Row(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .rippleClickable {
                            onDateChange()
                        },
                    text = dateText,
                    style = taskItemStyle.value.copy(color = TypographyColor.value),
                    fontSize = taskItemStyleSize.value
                )
                Text(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .wrapContentWidth()
                        .rippleClickable {
                            onTimeChange()
                        },
                    text = timeText,
                    style = taskItemStyle.value.copy(color = TypographyColor.value),
                    fontSize = taskItemStyleSize.value
                )
            }
        }
    }
}