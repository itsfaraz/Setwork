package com.designlife.setwork.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.presentation.components.rippleClickable
import com.designlife.setwork.ui.theme.PickerItemStyle
import com.designlife.setwork.ui.theme.UIComponentBackground
import com.designlife.setwork.ui.theme.pickerItemStyleSize

@Composable
fun CustomPickerComponent(
    itemList: List<String>,
    onCloseClick: () -> Unit,
    onItemClick: (index: Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rippleClickable { onCloseClick() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .wrapContentHeight()
                .background(UIComponentBackground.value, shape = RoundedCornerShape(12.dp)),
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(UIComponentBackground.value),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.padding(top = 6.dp))
                itemList.forEachIndexed { index, title ->
                    if (index != 0) {
                        Divider(color = Color.DarkGray, thickness = .2.dp)
                    }
                    Row(
                        modifier = Modifier
                            .rippleClickable {
                                onItemClick(index)
                                onCloseClick()
                            }
                            .padding(vertical = 10.dp)
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = title,
                            style = PickerItemStyle.value,
                            fontSize = pickerItemStyleSize.value
                        )
                    }

                }
                Spacer(modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}