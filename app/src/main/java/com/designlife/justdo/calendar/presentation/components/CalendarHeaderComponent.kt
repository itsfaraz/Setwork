package com.designlife.justdo.calendar.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.home.presentation.components.SelectedHeaderTitle
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarHeaderComponent(
    viewType: ViewType,
    today : Date,
    isLoading : Boolean,
    dateList : List<Date>,
    listState: LazyListState,
    onMenuEvent : () -> Unit,
    onTodayEvent : () -> Unit,
    onDateEvent : (index : Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(Shapes.cutBottomRoundedCorners(12.dp))
            .background(ComponentBackground.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .height(46.dp)
                        .width(60.dp)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        onMenuEvent()
                    }) {
                       Icon(painter = painterResource(R.drawable.ic_hamburger_menu), contentDescription = "Menu Icon", tint = ButtonPrimary.value)
                    }
                }

            }

            Spacer(modifier = Modifier.width(8.dp))
            SelectedHeaderTitle(
                viewType = viewType
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    onTodayEvent()
                }) {
                    Icon(modifier = Modifier.size(20.dp), painter = painterResource(id = R.drawable.ic_target),contentDescription = "Search Icon", tint = ButtonPrimary.value)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(18.dp))
            Text(text = getDate(today), style = TextStyle(fontSize = 26.sp, color = Color.Black, fontWeight = FontWeight.SemiBold))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = getYear(today), style = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.SemiBold))
        }
        Spacer(modifier = Modifier.height(14.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(horizontal = 8.dp),
            state = listState
        ) {
            items(
                count = dateList.size,
            ) { index ->

                CalendarDateItem(
                    isCurrent = dateList[index].time.equals(today.time),
                    date = dateList[index]
                ) {
                    onDateEvent(index)
                }
            }
        }
        if (isLoading){
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = ButtonPrimary.value,
                trackColor = ButtonPrimary.value.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )
        }

    }
}

private fun getDate(currentDate: Date) : String{
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    return SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time)
}

private fun getYear(currentDate: Date) : String{
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    return "${calendar.get(Calendar.YEAR)}"
}


@Preview
@Composable
fun CalendarHeaderComponentPreview(){
    val dateListState = rememberLazyListState()

    val calendar = Calendar.getInstance()

    val dateList: List<Date> = (1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        .map { day ->
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, day)
            }.time
        }

    CalendarHeaderComponent(
        today = Date(System.currentTimeMillis()),
        viewType = ViewType.CALENDAR,
        dateList = dateList,
        listState = dateListState,
        isLoading = true,
        onMenuEvent  = {},
        onTodayEvent  = {},
        onDateEvent = {}
    )
}