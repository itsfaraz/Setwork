package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.room.dao.TodoDao
import com.designlife.justdo.common.domain.converters.TodoConverters
import com.designlife.justdo.common.domain.entities.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TodoRepository(private val todoDao: TodoDao) {

    suspend fun insertTodo(todo: Todo) : Long{
        return todoDao.insertTodo(TodoConverters.getTodoEntity(todo))
    }

    suspend fun insertAllTodo(todoList: List<Todo>){
        return todoDao.insertAllTodo(
            todoList.map {
                TodoConverters.getTodoEntity(it)
            }
        )
    }

    fun getAllTodo() : Flow<List<Todo>> {
       return todoDao.getAllTodos().map { rawTodoList ->
           rawTodoList.map{ rawTodo ->
               TodoConverters.getTodo(rawTodo)
           }
       }
    }
}