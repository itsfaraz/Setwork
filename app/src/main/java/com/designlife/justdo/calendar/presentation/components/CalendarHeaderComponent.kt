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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.R
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.home.presentation.components.SelectedHeaderTitle
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.fontFamily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarHeaderComponent(
    viewType: ViewType,
    today : Date,
    currentDate: Date,
    currentMonth: String,
    currentYear: String,
    selectedIndex : Int,
    isLoading : Boolean,
    dateList : List<Date>,
    listState: LazyListState,
    onTodayEvent : () -> Unit,
    onDateEvent : (index : Int) -> Unit,
    onChangeVisibleDate: (date: Date) -> Unit,
    loadPreviousTrigger: () -> Unit,
    loadNextTrigger: () -> Unit,
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
        Spacer(modifier = Modifier.height(12.dp))
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
                    IconButton(modifier = Modifier.wrapContentSize().clip(CircleShape), onClick = {
                        onTodayEvent()
                    }) {
                        Icon(modifier = Modifier.size(22.dp), painter = painterResource(R.drawable.ic_today), contentDescription = "App Icon", tint = ButtonPrimary.value)
                    }
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            SelectedHeaderTitle(
                viewType = viewType
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(18.dp))
            androidx.compose.material.Text(text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = TypographyColor.value,
                        fontSize = 10.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Light
                    )
                ) {
                    append("MONTHLY ")
                }

                withStyle(
                    style = SpanStyle(
                        color = TypographyColor.value,
                        fontSize = 12.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("$currentMonth ")
                }

                withStyle(
                    style = SpanStyle(
                        color = TypographyColor.value,
                        fontSize = 10.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(currentYear)
                }
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(horizontal = 8.dp),
            state = listState
        ) {
            items(
                count = dateList.size,
                key = { index -> dateList[index].time }
            ) { index ->
                val date = dateList[index]
                CalendarDateItem(
                    isCurrent = date.time.equals(today.time),
                    isSelected = selectedIndex == index,
                    date = date
                ) {
                    onDateEvent(index)
                }

                if (index == 0) {
                    DisposableEffect(Unit) {
                        loadPreviousTrigger()
                        onDispose { }
                    }
                }

                if (index == dateList.size - 1) {
                    DisposableEffect(Unit) {
                        loadNextTrigger()
                        onDispose { }
                    }
                }
                onChangeVisibleDate(date)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (isLoading){
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = ButtonPrimary.value,
                trackColor = ButtonPrimary.value.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )
        }
    }
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

    val date = Date(System.currentTimeMillis())

    CalendarHeaderComponent(
        today = Date(System.currentTimeMillis()),
        viewType = ViewType.CALENDAR,
        dateList = dateList,
        listState = dateListState,
        selectedIndex = 1,
        isLoading = true,
        currentDate = date,
        currentMonth = "",
        currentYear = "",
        onTodayEvent  = {},
        onDateEvent = {},
        onChangeVisibleDate = {},
        loadNextTrigger = {},
        loadPreviousTrigger = {}
    )
}