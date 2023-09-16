package com.designlife.justdo.common.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.designlife.justdo.common.data.entities.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Transaction
    @Insert
    suspend fun insertTodo(todo: Todo) : Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTodo(todos: List<Todo>)

    @Transaction
    @Query("SELECT * FROM TODO")
    fun getAllTodos() : Flow<List<Todo>>

    @Transaction
    @Query("SELECT * FROM TODO WHERE todoId=:todoId")
    suspend fun getTodoById(todoId : Long) : Todo

    @Transaction
    @Query("UPDATE TODO SET isCompleted=:isCompleted WHERE todoId=:todoId")
    suspend fun updateTodoById(todoId : Long,isCompleted : Boolean = true)

    @Transaction
    @Query("DELETE FROM TODO WHERE todoId=:todoId")
    suspend fun deleteTodoById(todoId : Long)

}