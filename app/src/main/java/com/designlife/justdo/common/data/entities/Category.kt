package com.designlife.justdo.common.data.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId : Long = 0L,
    val name : String = "",
    val totalTodo : Int = 0,
    val totalCompleted : Int = 0,
    val color : String = "",
    val emoji : String = ""
)
