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

}