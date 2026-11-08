package com.designlife.justdo.home.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.presentation.components.rippleClickable
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.justdo.ui.theme.fontFamily
import com.designlife.justdo.ui.theme.headerStyle
import java.util.Date


@Composable
fun DateComponent(
    listState: LazyListState,
    currentDate: Date,
    currentMonth: String,
    currentYear: String,
    dateList: List<Date>,
    onEventClick: (index: Int) -> Unit,
    onChangeVisibleDate: (date: Date) -> Unit,
    loadPreviousTrigger: () -> Unit,
    loadNextTrigger: () -> Unit,
    selectedIndex: Int
) {
    val dateListSize = remember {
        mutableStateOf(dateList.size)
    }

    LaunchedEffect(dateList) {
        dateListSize.value = dateList.size
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = buildAnnotatedString {

                withStyle(
                    style = SpanStyle(
                        color = TypographyColor.value,
                        fontSize = 10.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Light
                    )
                ) {
                    append("CALENDAR ")
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
        Spacer(modifier = Modifier.height(20.dp))
        if (dateListSize.value != 0) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                state = listState
            ) {
                items(
                    count = dateList.size,
                    key = { index -> dateList[index].time }
                ) { index ->
                    val date = dateList[index]

                    DateItem(
                        isCurrent = date.time.equals(currentDate.time),
                        isSelected = index == selectedIndex,
                        date = date
                    ) {
                        onEventClick(index)
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
        }

    }

}

@Composable
fun DateItem(
    isCurrent: Boolean,
    date: Date,
    isSelected: Boolean,
    onEventClick: () -> Unit
) {

    val pair = IDateGenerator.getDayInfoFrom(date)
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(60.dp)
            .height(76.dp)
            .background(
                color = if (isSelected) ButtonPrimary.value else UIComponentBackground.value,
                shape = RoundedCornerShape(12)
            )
            .dashedBorder(
                strokeWidth = 1.dp,
                color = if (isCurrent && !isSelected) ButtonPrimary.value else Color.Transparent,
                cornerRadiusDp = 12.dp
            )
            .clip(RoundedCornerShape(12))
            .rippleClickable {
                onEventClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "${pair.first}",
            style = headerStyle.value.copy(
                color = if (isCurrent && !isSelected) ButtonPrimary.value else if (isSelected) Color.White else TypographyColor.value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pair.second,
            style = headerStyle.value.copy(
                color = if (isCurrent && !isSelected) ButtonPrimary.value else if (isSelected) Color.White else TypographyColor.value,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )

    }
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            }
        )
    }
)