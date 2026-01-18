package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.room.dao.TodoDao
import com.designlife.justdo.common.domain.converters.TodoConverters
import com.designlife.justdo.common.domain.entities.RawTodo
import com.designlife.justdo.common.domain.entities.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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

    suspend fun insertAllImportedTodo(todoList: List<com.designlife.justdo.common.data.entities.Todo>){
        return todoDao.insertAllTodo(todoList)
    }

    fun getAllRawTodo() : Flow<List<RawTodo>> {
        return todoDao.getAllTodos().map { todoList ->
            todoList.map {todo ->
                RawTodo(
                    todoId = todo.todoId,
                    date = todo.date,
                    createdOn = todo.createdOn)
            }
        }
    }

   suspend fun getAllTodo() : Flow<List<Todo>> {
       return todoDao.getAllTodos().map { rawTodoList ->
           rawTodoList.map{ rawTodo ->
               TodoConverters.getTodo(rawTodo)
           }
       }
    }

    suspend fun getTodoById(todoId : Int) : Todo {
        return TodoConverters.getTodo(todoDao.getTodoById(todoId.toLong()))
    }

    suspend fun getRawTodoById(todoId : Int) : RawTodo {
        val todo = todoDao.getTodoById(todoId.toLong())
        return RawTodo(date = todo.date,createdOn = todo.createdOn, categoryId = todo.categoryId)
    }

    suspend fun updateTodo(todoId : Int) : Unit {
        todoDao.updateTodoById(todoId.toLong(),true)
    }


    suspend fun updateArchiveTodo(todoIds : List<Int>) : Unit {
        todoIds.forEach {id ->
            todoDao.updateTodoById(id.toLong(),true)
        }
    }

    suspend fun deleteTodo(todoId : Int) : Unit {
        todoDao.deleteTodoById(todoId.toLong())
    }

}