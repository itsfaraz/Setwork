package com.designlife.justdo.calendar.data.dto

import androidx.compose.ui.graphics.Color
import com.designlife.justdo.calendar.data.static.EventType
import com.designlife.justdo.common.domain.entities.Category

data class LocalEvent(
    val eventId : Long = 0L,
    val eventInfo : EventInfo = EventInfo()
){
    data class EventInfo(
        val epoch : Long = 0L,
        val title : String = "",
        val description : String = "",
        val eventType : EventType? = EventType.UNKNOWN,
        val categoryColor: Color?= null,
    )
}
