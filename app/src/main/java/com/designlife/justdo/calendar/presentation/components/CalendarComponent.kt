package com.designlife.justdo.calendar.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.calendar.data.dto.LocalEvent
import com.designlife.justdo.calendar.data.static.EventType
import com.designlife.justdo.common.presentation.components.rippleClickable
import com.designlife.justdo.ui.theme.ButtonPrimary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private  data class EventDay(
    val date: LocalDate,
    val events: List<LocalEvent>
)

@Composable
fun CalendarComponent(
    localEvents : List<LocalEvent>,
    onLocalEvent : (eventId : Long) -> Unit
) {
    val eventDays = remember(localEvents) {
        localEvents
            .groupBy { event ->
                Instant.ofEpochMilli(event.eventInfo.epoch)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .toSortedMap()
            .map { (date, events) ->
                EventDay(
                    date = date,
                    events = events.sortedBy { it.eventInfo.epoch }
                )
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        eventDays.forEach { day ->

            item(
                key = "header-${day.date}"
            ) {
                CalendarDayHeader(
                    date = day.date
                )
            }

            items(
                items = day.events,
                key = { it.eventId }
            ) { event ->
                CalendarEventItem(
                    event = event
                ){ onLocalEvent(it) }
            }
        }
    }
}

@Composable
private fun CalendarDayHeader(
    date: LocalDate
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(.2f),
            text = date.dayOfMonth.toString(),
            style = TextStyle(
                fontSize = 28.sp,
                color = ButtonPrimary.value,
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = date.dayOfWeek
                .getDisplayName(
                    java.time.format.TextStyle.FULL,
                    Locale.getDefault()
                ),
            style = TextStyle(
                fontSize = 18.sp,
                color = ButtonPrimary.value,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun CalendarEventItem(
    event: LocalEvent,
    onEvent : (eventId : Long) -> Unit
) {
    val eventDateTime = remember(event.eventInfo.epoch) {
        Instant.ofEpochMilli(event.eventInfo.epoch)
            .atZone(ZoneId.systemDefault())
    }

    val eventTime = remember(event.eventInfo.epoch) {
        eventDateTime.format(
            DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ENGLISH
            )
        )
    }

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .rippleClickable(onClick = {onEvent(event.eventId)}),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(.2f)
                .wrapContentHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                modifier = Modifier.padding(top = 22.dp),
                text = eventTime,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    event?.eventInfo?.categoryColor ?: Color.DarkGray
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = event.eventInfo.title
                        .take(36)
                        .let { if (event.eventInfo.title.length > 36) "$it ..." else it },
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.eventInfo.description
                        .take(49)
                        .let {
                            if (event.eventInfo.description.length > 49) "$it ..."
                            else it
                        },
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}


@Preview
@Composable fun CalendarComponentPreview() {
    val now = System.currentTimeMillis()

    val events = listOf(
        LocalEvent(
            eventId = 100001L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now,
                title = "Team stand-up",
                description = "Daily engineering sync with the Orange Labs team.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100011L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now,
                title = "Team stand-up",
                description = "Daily engineering sync with the Orange Labs team.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100002L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 30 * 60 * 1000L,
                title = "Review SysLiv dashboard",
                description = "Check pharmacy dashboard performance, billing flow and inventory updates.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100003L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 60 * 60 * 1000L,
                title = "Call with Jay",
                description = "Discuss product requirements and the next development milestone.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100004L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 2 * 60 * 60 * 1000L,
                title = "Find Roger's notes",
                description = "Locate the architecture notes shared during the previous project discussion.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100005L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 3 * 60 * 60 * 1000L,
                title = "Laura — product discussion",
                description = "Review the upcoming product changes and collect feedback.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100006L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 5 * 60 * 60 * 1000L,
                title = "Fix calendar animation",
                description = "Improve the horizontal drag interaction and spring-back behaviour.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100007L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 24 * 60 * 60 * 1000L,
                title = "Android interview preparation",
                description = "Review AOSP framework, HAL, Binder, SystemServer and application lifecycle.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100008L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 26 * 60 * 60 * 1000L,
                title = "Rust + AOSP",
                description = "Study Rust integration across Android platform components.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100009L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 48 * 60 * 60 * 1000L,
                title = "Publish Setwork update",
                description = "Prepare release notes, screenshots and F-Droid metadata.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        ),
        LocalEvent(
            eventId = 100010L,
            eventInfo = LocalEvent.EventInfo(
                epoch = now + 72 * 60 * 60 * 1000L,
                title = "Long event title for calendar UI testing — meeting with Jay, Roger and Laura",
                description = "This deliberately long description is useful for testing text wrapping, clipping and multi-line event layouts inside the calendar.",
                eventType = EventType.TASK,
                categoryColor = null
            )
        )
    )

    CalendarComponent(localEvents = events){ }
}

