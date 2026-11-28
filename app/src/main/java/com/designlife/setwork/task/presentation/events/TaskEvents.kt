package com.designlife.setwork.task.presentation.events

import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.utils.enums.RepeatType
import com.designlife.setwork.home.presentation.events.HomeEvents

sealed class TaskEvents{
    data class OnTitleChange(val value : String) : TaskEvents()
    data class OnNoteChange(val value : String) : TaskEvents()
    data class OnDateChange(val value : String) : TaskEvents()
    data class CreateTask(val repeatType: RepeatType,val category : Category) : TaskEvents()
    data class OnTimeChange(val value : String) : TaskEvents()
    data class OnProgressBarToggle(val toggleValue : Boolean) : TaskEvents()
    data class LoadTodoById(val todoId : Int) : TaskEvents()
    data class MarkTaskDone(val todoId : Int)  : TaskEvents()
    data class DeleteTaskPopup(val state : Boolean)  : TaskEvents()
    data class DeleteTasksEvent(val taskId : Int, val allOccurrence : Boolean)  : TaskEvents()
    object DeletePopToggleOff  : TaskEvents()
    data class OnCategoryIndexChange(val index : Int)  : TaskEvents()
}
