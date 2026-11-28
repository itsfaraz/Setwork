package com.designlife.setwork.common.domain.entities

import androidx.compose.ui.graphics.Color
import com.designlife.setwork.common.data.entities.Category
import com.designlife.setwork.common.domain.converters.ColorConverter


data class Category(
    val id : Long = 0L,
    val name : String = "",
    val totalTodo : Int = 0,
    val totalCompleted : Int = 0,
    val emoji : String = "",
    val color : Color = Color(0)
){
    override fun toString(): String {
        return "id : ${id} :: name : ${name} :: totalTodo : ${totalTodo} :: totalCompleted : ${totalCompleted} :: emoji : ${emoji} :: color : ${color}"
    }

    fun toCategory() : Category{
        return Category(
            categoryId = this.id,
            name = this.name,
            totalTodo = this.totalTodo,
            totalCompleted = this.totalCompleted,
            color = ColorConverter.serializeColor(this.color),
            emoji = this.emoji
        )
    }
}
