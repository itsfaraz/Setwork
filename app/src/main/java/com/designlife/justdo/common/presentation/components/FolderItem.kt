package com.designlife.justdo.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.utils.camelCase
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TaskItemLabelColor
import com.designlife.justdo.ui.theme.folderTextStyle


@Composable
fun FolderItem(
    folderName : String,
    emoji : String,
    colorTheme : Color,
    isSelected : Boolean,
    onFolderEvent : () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .wrapContentWidth()
            .height(36.dp)
            .clickable {
                onFolderEvent()
            }
            .clip(RoundedCornerShape(100))
            .background(if (isSelected) ButtonPrimary else Color.White)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(100)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .size(16.dp),
                shape = RoundedCornerShape(100),
                backgroundColor = colorTheme,
                elevation = 10.dp
            ) {}
            Text(
                text = emoji,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = (if(folderName.length > 14)"${folderName.substring(0,12)} ..." else folderName).camelCase(),
            style = folderTextStyle.copy(color = if (isSelected) Color.White else TaskItemLabelColor)
        )
    }
}