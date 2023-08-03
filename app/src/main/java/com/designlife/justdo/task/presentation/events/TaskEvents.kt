package com.designlife.justdo.task.presentation.events

import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.utils.enums.RepeatType

sealed class TaskEvents{
    data class OnTitleChange(val value : String) : TaskEvents()
    data class OnNoteChange(val value : String) : TaskEvents()
    data class OnDateChange(val value : String) : TaskEvents()
    data class CreateTask(val repeatType: RepeatType,val category : Category) : TaskEvents()
    data class OnTimeChange(val value : String) : TaskEvents()
}
