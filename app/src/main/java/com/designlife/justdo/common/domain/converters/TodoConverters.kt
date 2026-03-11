package com.designlife.justdo.common.domain.converters

import com.designlife.justdo.common.data.room.dao.TodoDao
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.RawTodo
import com.designlife.justdo.common.domain.entities.Todo
import kotlin.math.absoluteValue

object TodoConverters {
    fun getTodoEntity(todo: Todo) : com.designlife.justdo.common.data.entities.Todo{
        return com.designlife.justdo.common.data.entities.Todo(
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

    fun getTodo(todo: com.designlife.justdo.common.data.entities.Todo) : Todo{
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