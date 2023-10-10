package com.designlife.justdo.common.domain.converters

import com.designlife.justdo.common.domain.entities.Category


object CategoryConverter {
    fun getCategoryEntity(category: Category) : com.designlife.justdo.common.data.entities.Category{
        return com.designlife.justdo.common.data.entities.Category(
            categoryId = category.id,
            name = category.name,
            totalTodo = category.totalTodo,
            totalCompleted = category.totalCompleted,
            color = ColorConverter.serializeColor(category.color),
            emoji = category.emoji
        )
    }


    fun getCategory(category: com.designlife.justdo.common.data.entities.Category) : Category {
        return Category(
            id = category.categoryId,
            name = category.name,
            totalTodo = category.totalTodo,
            totalCompleted = category.totalCompleted,
            color = ColorConverter.deserializeColor(category.color),
            emoji = category.emoji
        )
    }


}