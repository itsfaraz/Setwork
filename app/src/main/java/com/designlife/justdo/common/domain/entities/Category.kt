package com.designlife.justdo.common.domain.entities

import androidx.compose.ui.graphics.Color


data class Category(
    val id : Long = 0L,
    val name : String,
    val totalTodo : Int,
    val totalCompleted : Int,
    val emoji : String = "",
    val color : Color
)
