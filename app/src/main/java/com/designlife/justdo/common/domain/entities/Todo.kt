package com.designlife.justdo.common.domain.entities

import com.designlife.justdo.common.data.entities.Todo
import java.util.Date
import kotlin.String

data class Todo(
    val todoId : Int,
    val title : String,
    val date : Date,
    val note : String,
    val categoryId : Long,
    val isRepeated : Boolean,
    val repeatIndex : Int,
    val isCompleted : Boolean,
    val createdOn : Long,
    val isArchive : Boolean,
){
    fun toTodo() : Todo{
        return Todo(
            todoId = this.todoId.toLong(),
            title = this.title,
            date = this.date.time,
            note = this.note,
            isRepeated = this.isRepeated,
            repeatIndex = this.repeatIndex,
            isCompleted = this.isCompleted,
            categoryId = this.categoryId,
            createdOn = this.createdOn,
            isArchive = this.isArchive
        )
    }
}
