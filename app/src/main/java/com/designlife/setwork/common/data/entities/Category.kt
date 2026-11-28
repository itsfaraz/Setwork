package com.designlife.setwork.common.data.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
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
