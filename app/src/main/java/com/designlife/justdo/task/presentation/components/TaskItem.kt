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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun TaskItemView(
    hasIcon : Boolean,
    color : Color,
    @DrawableRes icon : Int,
    labelText : String,
    inputText : String,
    placeholder : String = "",
    isNote : Boolean = false,
    isClickable : Boolean = false,
    isOverview : Boolean = false,
    onInputChange : (value : String) -> Unit,
    onClickEvent : () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                       if (isClickable){
                           onClickEvent();
                       }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        if (hasIcon){
            Icon(painter = painterResource(id = icon), contentDescription = "Item Icon", modifier = Modifier.size(14.dp,height=18.dp), tint = TaskItemLabelColor)
        }else{
            Card(
                modifier = Modifier.size(15.dp),
                backgroundColor = color,
                shape = RoundedCornerShape(100),
                elevation = 10.dp
            ) {}
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isClickable) .94F else 1F)
                .fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxWidth(),
                text =  labelText,
                style = taskItemLabelStyle
            )
            if (isClickable){
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = inputText,
                    style = taskItemStyle
                )
            }else{
                CustomTaskInput(
                    value = inputText,
                    placeholder = placeholder,
                    multiline = isNote,
                    onValueChange = {onInputChange(it)}
                )
            }
        }
        if (isClickable && !isOverview){
            Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "Item Icon", modifier = Modifier.size(15.dp), tint = TaskItemLabelColor)
        }
    }
}