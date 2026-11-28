package com.designlife.setwork.archive.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.setwork.R
import com.designlife.setwork.calendar.data.dto.LocalEvent
import com.designlife.setwork.calendar.data.static.EventType
import com.designlife.setwork.common.presentation.components.rippleClickable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class EventDay(
    val date: LocalDate,
    val events: List<LocalEvent>
)

@Composable
fun ArchiveComponent(
    listState: LazyListState,
    localEvents: List<LocalEvent>,
    onLocalEvent: (event: LocalEvent) -> Unit,
    onDeleteEvent: (event: LocalEvent) -> Unit
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
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        eventDays.forEachIndexed { _,  day ->
            itemsIndexed(
                items = day.events,
                key = { _, item -> item.eventId + item.eventInfo.epoch }
            ) { _ , event ->
                ArchiveEventItem(
                    event = event,
                    onDeleteEvent = {
                        onDeleteEvent(event)
                    },
                    onEvent = {
                        onLocalEvent(event)
                    }
                )
            }
        }

    }
}

@Composable
private fun ArchiveEventItem(
    event: LocalEvent,
    onEvent: (eventId: Long) -> Unit,
    onDeleteEvent : (eventId : Long) -> Unit
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
            .rippleClickable(onClick = { onEvent(event.eventId) }),
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
                .height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(.7F)
                        .fillMaxHeight()
                        .padding(start = 8.dp),
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
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(modifier = Modifier.size(18.dp), painter = painterResource(when(event.eventInfo.eventType){
                        EventType.DECK -> { R.drawable.ic_mini_deck }
                        EventType.NOTE -> { R.drawable.ic_mini_note }
                        EventType.TASK -> { R.drawable.ic_mini_check }
                        else -> { R.drawable.ic_mini_check }
                    }), contentDescription = "Icon")

                    IconButton(onClick = {
                        onDeleteEvent(event.eventId)
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_delete), contentDescription = "Delete Icon")
                    }
                }

            }

        }
    }
}


@Preview
@Composable
fun ArchiveComponentPreview() {
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
    val listState = rememberLazyListState()
    ArchiveComponent(listState = listState, localEvents = events, onLocalEvent = { _ -> }, onDeleteEvent = {})
}

