package com.designlife.setwork.home.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.designlife.setwork.common.presentation.components.rippleClickable
import com.designlife.setwork.ui.theme.SettingItemStyle
import com.designlife.setwork.ui.theme.TypographyColor
import com.designlife.setwork.ui.theme.settingItemStyleSize

@Composable
fun SettingItemComponent(
    @DrawableRes drawableIcon: Int,
    title: String,
    onSettingItemEvent: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .rippleClickable { onSettingItemEvent() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            modifier = Modifier.size(13.dp),
            painter = painterResource(id = drawableIcon),
            contentDescription = "Feedback Icon",
            tint = SettingItemStyle.value.color
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = SettingItemStyle.value.copy(
                color = TypographyColor.value,
                fontSize = settingItemStyleSize.value
            )
        )
    }
}