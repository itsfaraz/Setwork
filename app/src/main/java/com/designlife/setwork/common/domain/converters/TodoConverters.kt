package com.designlife.setwork.common.domain.converters

import com.designlife.setwork.common.data.room.dao.TodoDao
import com.designlife.setwork.common.domain.calendar.IDateGenerator
import com.designlife.setwork.common.domain.entities.RawTodo
import com.designlife.setwork.common.domain.entities.Todo
import kotlin.math.absoluteValue

object TodoConverters {
    fun getTodoEntity(todo: Todo) : com.designlife.setwork.common.data.entities.Todo{
        return com.designlife.setwork.common.data.entities.Todo(
            todoId = todo.todoId.toLong(),
            title = todo.title,
            date = IDateGenerator.getEpochFromDate(todo.date),
            note = todo.note,
            isRepeated = todo.isRepeated,
            isCompleted = todo.isCompleted,
            categoryId = todo.categoryId,
            createdOn = todo.createdOn,
            repeatIndex = todo.repeatIndex
        )
    }

    fun getTodo(todo: com.designlife.setwork.common.data.entities.Todo) : Todo{
        return Todo(
            todoId = todo.todoId.toInt(),
            title = todo.title,
            date = IDateGenerator.getDateFromEpoch(todo.date),
            note = todo.note,
            isRepeated = todo.isRepeated,
            isCompleted = todo.isCompleted,
            categoryId = todo.categoryId,
            createdOn = todo.createdOn,
            repeatIndex = todo.repeatIndex,
            isArchive = todo.isArchive
        )
    }

    fun getRawTodo(todo: RawTodo) : RawTodo{
        return RawTodo(
            todoId = todo.todoId,
            title = todo.title,
            date = todo.date,
            note = todo.note,
            isRepeated = todo.isRepeated,
            isCompleted = todo.isCompleted,
            categoryId = todo.categoryId,
            createdOn = todo.createdOn,
            repeatIndex = todo.repeatIndex
        )
    }
}