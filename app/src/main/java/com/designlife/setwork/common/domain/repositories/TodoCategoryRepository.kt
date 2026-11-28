package com.designlife.setwork.common.domain.repositories


import com.designlife.setwork.common.domain.entities.Category
import com.designlife.setwork.common.data.room.dao.CategoryDao
import com.designlife.setwork.common.data.room.dao.TodoCategoryJunctionDao
import com.designlife.setwork.common.data.room.dao.TodoDao
import com.designlife.setwork.common.domain.converters.CategoryConverter
import com.designlife.setwork.common.domain.converters.TodoConverters
import com.designlife.setwork.common.domain.entities.Todo

class TodoCategoryRepository(
   private val todoCategoryDao : TodoCategoryJunctionDao,
   private val todoDao : TodoDao,
   private val categoryDao: CategoryDao,
) {

    suspend fun getTodoById(
        todoId : Int,
    ) : Pair<Todo, Category>{
        val (todo,category) = todoCategoryDao.getTodoById(todoId = todoId.toLong(), todoDao = todoDao, categoryDao = categoryDao)
        return Pair(TodoConverters.getTodo(todo), CategoryConverter.getCategory(category))
    }

    suspend fun deleteTodoById(
        todoId : Int,
        category: Category
    ){
        todoCategoryDao.deleteTodoById(todoId = todoId.toLong(), category = CategoryConverter.getCategoryEntity(category), todoDao = todoDao, categoryDao = categoryDao)
    }
}