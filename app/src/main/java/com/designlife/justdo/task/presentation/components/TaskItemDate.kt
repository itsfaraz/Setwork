package com.designlife.justdo.task.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.justdo.R
import com.designlife.justdo.common.presentation.components.CustomTaskInput
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.taskItemLabelStyle
import com.designlife.justdo.ui.theme.taskItemStyle

@Composable
fun TaskItemDate(
    dateText : String,
    timeText : String,
    @DrawableRes icon : Int,
    labelText : String,
    isClickable : Boolean,
    onDateChange : () -> Unit,
    onTimeChange : () -> Unit,
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
        Icon(painter = painterResource(id = icon), contentDescription = "Item Icon", modifier = Modifier.size(14.dp,height=18.dp), tint = TaskItemLabelColor)
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxWidth(),
                text =  labelText,
                style = taskItemLabelStyle
            )
            Row(
                modifier = Modifier.padding(start = 6.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.wrapContentWidth().clickable {
                        if (isClickable)
                            onDateChange()
                    },
                    text = dateText,
                    style = taskItemStyle
                )
                Text(
                    modifier = Modifier.padding(end = 4.dp).wrapContentWidth().clickable {
                        if(isClickable)
                            onTimeChange()
                    },
                    text = timeText,
                    style = taskItemStyle
                )
            }
        }
    }
}