package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.fontFamily
import com.designlife.justdo.ui.theme.headerStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun DateComponent(
    listState : LazyListState,
    currentDate : Date,
    currentMonth : String,
    currentYear : String,
    dateList : List<Date>,
    onEventClick : (index : Int) -> Unit,
    onChangeVisibleDate : (date : Date) -> Unit,
    loadPreviousTrigger : () -> Unit,
    loadNextTrigger : () -> Unit,
    selectedIndex : Int
) {

    val firstVisibleIndex = remember {
        mutableStateOf(listState.firstVisibleItemIndex)
    }

    val dateListSize = remember {
        mutableStateOf(dateList.size)
    }

    LaunchedEffect(dateList){
        dateListSize.value = dateList.size
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = buildAnnotatedString {

                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = FontWeight.Light)){
                    append("CALENDAR ")
                }


                withStyle(style = SpanStyle(color = Color.Black, fontSize = 12.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold)){
                    append("$currentMonth ")
                }

                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold)){
                    append(currentYear)
                }

            })
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (dateListSize.value != 0){
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(horizontal = 12.dp),
                state = listState
            ){
                items(
                    count = dateList.size,
//                    key = {}
                ){ index ->

                    DateItem(isCurrent = dateList[index].time.equals(currentDate.time), isSelected = index == selectedIndex, date = dateList[index]) {
                        onEventClick(index)
                    }


                    if (index == 0){
                        DisposableEffect(Unit){
                            loadPreviousTrigger()
                            onDispose {  }
                        }
                    }

                    if (index == dateList.size-1){
                        DisposableEffect(Unit){
                            loadNextTrigger()
                            onDispose {  }
                        }
                    }
                    onChangeVisibleDate(dateList[index])
                }
            }
        }

    }

}

@Composable
fun DateItem(
    isCurrent : Boolean,
    date : Date,
    isSelected : Boolean,
    onEventClick : () -> Unit
) {

    val pair = IDateGenerator.getDayInfoFrom(date)
    Column(modifier = Modifier
        .padding(horizontal = 8.dp)
        .width(60.dp)
        .height(76.dp)
        .background(
            color = if (isCurrent) ButtonPrimary else if (isSelected) ButtonHighLightPrimary else Color.White,
            shape = RoundedCornerShape(12)
        )
        .clickable {
            onEventClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "${pair.first}",
            style = headerStyle.copy(
                color = if (isCurrent) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pair.second,
            style = headerStyle.copy(
                color = if (isCurrent) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )

    }
}

@Preview
@Composable
fun DateComponentPreview() {

//    val dateGenerator = IDateGenerator()
//
//    var list = listOf<Date>()
//    LaunchedEffect(Unit){
//        CoroutineScope(Dispatchers.IO).launch {
//            dateGenerator.getDateList().collect{
//                list = it
//            }
//        }
//    }
//
//    val currentMonth = remember {
//        mutableStateOf(IDateGenerator.getMonthFromDate(dateGenerator.getToday()))
//    }
//    Column(
//        modifier = Modifier.fillMaxWidth().background(color = Color.Gray),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        DateComponent(
//            listState = rememberLazyListState(),
//            currentDate = dateGenerator.getToday(),
//            currentMonth = currentMonth.value,
//            dateList = list,
//            onEventClick = {index -> },
//            onChangeVisibleMonth = { month ->
//                currentMonth.value = month
//            }
//        )
//    }

}