package com.designlife.setwork.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.utils.enums.ScreenType
import com.designlife.setwork.ui.theme.ComponentBackground
import com.designlife.setwork.ui.theme.IconColor
import com.designlife.setwork.ui.theme.Shapes
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.cutBottomRoundedCorners
import com.designlife.setwork.ui.theme.headerStyle

@Composable
fun CategoryHeader(
    headerTitle: String,
    screenType: ScreenType,
    onCloseEvent: () -> Unit,
    onEditEvent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp, max = 72.dp)
            .clip(Shapes.cutBottomRoundedCorners(16.dp))
            .background(ComponentBackground.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(.7F),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { onCloseEvent() }) {
                    Column(
                        modifier = Modifier
                            .size(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                            tint = IconColor.value
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = headerTitle,
                    style = headerStyle.value.copy(color = TypographyColor.value)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(1F),
                horizontalArrangement = Arrangement.End
            ) {
                if (screenType == ScreenType.CATEGORY) {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(26.dp), onClick = {
                            onEditEvent()
                        }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Pencil Icon",
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }

}