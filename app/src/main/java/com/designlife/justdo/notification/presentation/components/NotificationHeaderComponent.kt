package com.designlife.justdo.notification.presentation.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.designlife.justdo.home.presentation.components.SelectedHeaderTitle
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.commonStyleSize
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import java.util.Calendar
import java.util.Date

@Composable
fun NotificationHeaderComponent(
    notificationCount : Int,
    onEventClick : () -> Unit,
    currentDate : Date,
    searchIconVisibility : Boolean,
    onSearchIconClick : () -> Unit,
    viewType : ViewType,
    onChatIconEvent : () -> Unit,
    onNotificationIconEvent : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(Shapes.cutBottomRoundedCorners(15.dp))
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
                        .background(color = ButtonPrimary.value, shape = RoundedCornerShape(topEnd = 100.dp, bottomEnd = 100.dp))
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        onEventClick()
                    }) {
                        Column(
                            modifier = Modifier
                                .size(26.dp)
                                .background(color = Color.White, RoundedCornerShape(100)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = getDateInt(currentDate),
                                color = ButtonPrimary.value,
                                style = TextStyle(
                                    fontSize = commonStyleSize.value,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
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
                if (viewType == ViewType.TASK){
                    IconButton(onClick = {
                        onChatIconEvent()
                    }) {
                        Image(modifier = Modifier.size(20.dp), painter = painterResource(id = R.drawable.ic_ai_chat),contentDescription = "AI Chat Icon")
                    }
                }else{
                    if (searchIconVisibility){
                        IconButton(onClick = {
                            onSearchIconClick()
                        }) {
                            Icon(modifier = Modifier.size(20.dp), painter = painterResource(id = R.drawable.ic_search),contentDescription = "Search Icon", tint = ButtonPrimary.value)
                        }
                    }
                }
                IconButton(onClick = {
                    onNotificationIconEvent()
                }) {
                    Box(modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 6.dp),
                        contentAlignment = Alignment.Center) {
                        Icon(modifier = Modifier.size(24.dp), painter = painterResource(id = R.drawable.ic_notification),contentDescription = "Notification Icon",tint = ButtonPrimary.value)
                        if (notificationCount > 0){
                            Text(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 22.dp), text = "●", style = TextStyle(color = ButtonPrimary.value, fontSize = 12.sp), textAlign = TextAlign.End)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

private fun getDateInt(currentDate: Date) : String{
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    return calendar.get(Calendar.DAY_OF_MONTH).toString()
}

@Preview
@Composable
fun NotificationHeaderComponentPreview(){
    NotificationHeaderComponent(
        notificationCount = 121,
        onEventClick = {},
        currentDate = Date(System.currentTimeMillis()),
        searchIconVisibility = true,
        onSearchIconClick = {},
        ViewType.TASK, onChatIconEvent = {},
        onNotificationIconEvent = {}
    )
}