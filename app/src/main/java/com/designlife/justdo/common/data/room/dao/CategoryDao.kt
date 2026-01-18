package com.designlife.justdo.common.data.room.dao

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.designlife.justdo.common.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Insert
    suspend fun insertCategory(category: Category) : Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<Category>)

    @Transaction
    @Query("SELECT * FROM CATEGORY")
    fun getAllCategories() : Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM CATEGORY WHERE categoryId=:categoryId")
    suspend fun getCategoryById(categoryId : Long) : Category

    @Transaction
    @Update
    suspend fun updateCategory(category: Category)

    @Transaction
    @Query("DELETE FROM Category WHERE categoryId =:categoryId ")
    suspend fun deleteCategoryById(categoryId : Long)

    @Transaction
    @Query("UPDATE Category SET totalTodo=:totalTodo, totalCompleted=:totalCompleted WHERE categoryId=:categoryId ")
    suspend fun updateCategoryById(
        categoryId : Long,
        totalTodo : Int,
        totalCompleted : Int
        ) : Unit

    @Query("UPDATE Category SET totalCompleted=:completedCount WHERE categoryId=:categoryId ")
    suspend fun updateCategoryCountById(categoryId : Long,completedCount : Int) : Unit
}