package com.designlife.justdo.common.data.room.dao

import androidx.room.Dao
import androidx.room.Transaction
import com.designlife.justdo.common.data.entities.Category
import com.designlife.justdo.common.data.entities.Todo

@Dao
interface TodoCategoryJunctionDao {
    @Transaction
    suspend fun deleteTodoById(
        todoId : Long,
        category: Category,
        todoDao: TodoDao,
        categoryDao: CategoryDao
    ){

        todoDao.deleteTodoById(todoId)
        categoryDao.updateCategory(category)
    }

    @Transaction
    suspend fun getTodoById(
        todoId : Long,
        todoDao: TodoDao,
        categoryDao: CategoryDao
    ) : Pair<Todo, Category>{
        val todo = todoDao.getTodoById(todoId)
        return Pair(todo,categoryDao.getCategoryById(todo.categoryId))
    }
}