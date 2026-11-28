package com.designlife.setwork.common.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithTodos(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "todoId"
    )
    val todos : List<Todo>
)
