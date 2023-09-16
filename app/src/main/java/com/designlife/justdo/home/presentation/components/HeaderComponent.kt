package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.headerStyle
import java.util.Calendar
import java.util.Date

@Composable
fun HeaderComponent(
    headerText : String,
    onEventClick : () -> Unit,
    currentDate : Date,
    searchIconVisibility : Boolean,
    onSearchIconClick : () -> Unit,
    currentDayButton : Boolean = false,
    viewType : ViewType,
    onViewChange : (viewType : ViewType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(Shapes.cutBottomRoundedCorners(15.dp))
            .background(Color.White),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .size(25.dp)
                    .background(color = ButtonPrimary, RoundedCornerShape(100))
                    .clickable { onEventClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getDateInt(currentDate),
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = headerText,
//                style = headerStyle,
//                modifier = Modifier.fillMaxWidth()
//            )
            HeaderDropDownComponent(
                viewType = viewType,
                onViewChange = {onViewChange(it)}
            )
            if (searchIconVisibility){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        onSearchIconClick()
                    }) {
                        Icon(modifier = Modifier.size(20.dp), painter = painterResource(id = R.drawable.ic_search),contentDescription = "Search Icon", tint = ButtonPrimary)
                    }
                }
            }
        }
    }
}

private fun getDateInt(currentDate: Date) : String{
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    return calendar.get(Calendar.DAY_OF_MONTH).toString()
}

