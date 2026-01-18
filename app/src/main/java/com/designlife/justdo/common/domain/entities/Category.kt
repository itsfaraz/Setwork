package com.designlife.justdo.common.domain.entities

import androidx.compose.ui.graphics.Color


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
}
