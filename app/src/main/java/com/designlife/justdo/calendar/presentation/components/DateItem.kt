package com.designlife.justdo.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.presentation.components.rippleClickable
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.headerStyle
import java.util.Date

@Composable
fun CalendarDateItem(
    isCurrent: Boolean,
    isSelected : Boolean,
    date: Date,
    onEventClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(48.dp)
            .height(48.dp)
            .background(
                color = if (isSelected || isCurrent) ButtonPrimary.value else UIComponentBackground.value,
                shape = RoundedCornerShape(100)
            )
            .rippleClickable() {
                onEventClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${date.date}",
            style = headerStyle.value.copy(
                color = if (isCurrent) Color.White else TypographyColor.value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
    }
}