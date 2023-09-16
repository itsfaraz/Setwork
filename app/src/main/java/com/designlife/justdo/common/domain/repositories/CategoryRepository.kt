package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.room.dao.CategoryDao
import com.designlife.justdo.common.domain.converters.CategoryConverter
import com.designlife.justdo.common.domain.entities.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    fun getAllCategory() : Flow<List<Category>>{
        return categoryDao.getAllCategories().map { rawCategoryList ->
            rawCategoryList.map {rawCategory ->
                CategoryConverter.getCategory(rawCategory)
            }
        }
    }

    suspend fun insertCategory(category: Category) : Long{
        return categoryDao.insertCategory(CategoryConverter.getCategoryEntity(category))
    }

    suspend fun getCategoryById(categoryId : Long) : Category{
        return CategoryConverter.getCategory(categoryDao.getCategoryById(categoryId))
    }

    suspend fun updateCategory(category : Category){
        return categoryDao.updateCategory(CategoryConverter.getCategoryEntity(category))
    }


    private suspend fun updateCategoryCountById(categoryId: Long, completedCount : Int){
        categoryDao.updateCategoryCountById(categoryId, completedCount)
    }

    suspend fun updateCategoryCount(categoryMap : Map<Long,Int>){
        categoryMap.keys.forEach {key ->
            updateCategoryCountById(key,categoryMap.get(key)!!)
        }
    }
}